package com.example.siads.hooks

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.siads.config.DataEnvironmentMode
import com.example.siads.config.FirebaseConfig
import com.example.siads.config.FirebaseIntegrationStatus
import com.example.siads.services.DemoSiadsRepository
import com.example.siads.services.DriveFileSummary
import com.example.siads.services.GoogleDriveService
import com.example.siads.services.SiadsRepository
import com.example.siads.services.SiadsSnapshot
import com.example.siads.types.CategoryType
import com.example.siads.types.UserProfile
import com.example.siads.types.UserRole
import com.example.siads.types.VerificationStatus
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ResponsiveLayoutInfo(
    val isCompact: Boolean,
    val isMedium: Boolean,
    val isExpanded: Boolean,
    val showPermanentSidebar: Boolean
)

/**
 * Hook responsif untuk menyesuaikan tata letak Android (Compact) vs Tablet/Laptop (Medium/Expanded).
 */
@Composable
fun rememberResponsiveLayoutInfo(maxWidth: Dp): ResponsiveLayoutInfo {
    val isCompact = maxWidth < 600.dp
    val isMedium = maxWidth >= 600.dp && maxWidth < 840.dp
    val isExpanded = maxWidth >= 840.dp
    return ResponsiveLayoutInfo(
        isCompact = isCompact,
        isMedium = isMedium,
        isExpanded = isExpanded,
        showPermanentSidebar = maxWidth >= 840.dp
    )
}

data class SiadsUiState(
    val snapshot: SiadsSnapshot,
    val currentUser: UserProfile? = null,
    val firebaseStatus: FirebaseIntegrationStatus,
    val feedbackMessage: String? = null,
    val isErrorFeedback: Boolean = false,
    val selectedCategoryFilter: CategoryType? = null,
    val driveAccessToken: String? = null,
    val isDriveUploading: Boolean = false,
    val driveFiles: List<DriveFileSummary> = emptyList()
) {
    val isAuthenticated: Boolean get() = currentUser != null
    val isDemoMode: Boolean get() = snapshot.mode == DataEnvironmentMode.DEMO_PREVIEW
    val isDriveConnected: Boolean get() = !driveAccessToken.isNullOrBlank()
}

class SiadsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SiadsRepository = DemoSiadsRepository()
    private val googleDriveService = GoogleDriveService()
    private val initialFirebaseStatus = FirebaseConfig.inspectStatus(application.applicationContext)

    private val _uiState = MutableStateFlow(
        SiadsUiState(
            snapshot = repository.getSnapshot(),
            currentUser = null,
            firebaseStatus = initialFirebaseStatus
        )
    )
    val uiState: StateFlow<SiadsUiState> = _uiState.asStateFlow()

    fun buildDriveAuthRequest(): AuthorizationRequest = googleDriveService.buildAuthorizationRequest()

    fun onDriveAuthorized(accessToken: String) {
        _uiState.update {
            it.copy(
                driveAccessToken = accessToken,
                feedbackMessage = "Google Drive terhubung. Semua file unggahan akan disimpan ke folder sekolah.",
                isErrorFeedback = false
            )
        }
        refreshDriveFolderFiles()
    }

    fun onDriveAuthorizationError(message: String) {
        _uiState.update {
            it.copy(
                feedbackMessage = message,
                isErrorFeedback = true
            )
        }
    }

    fun refreshDriveFolderFiles() {
        val token = _uiState.value.driveAccessToken ?: return
        viewModelScope.launch {
            val result = googleDriveService.listFilesInTargetFolder(token)
            result.onSuccess { files ->
                _uiState.update { it.copy(driveFiles = files) }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        feedbackMessage = err.message ?: "Gagal membaca daftar file Google Drive.",
                        isErrorFeedback = true
                    )
                }
            }
        }
    }

    fun loginWithEmail(email: String, password: String): Boolean {
        val result = repository.loginWithEmail(email, password)
        _uiState.update { state ->
            state.copy(
                snapshot = repository.getSnapshot(),
                currentUser = result.user ?: state.currentUser,
                feedbackMessage = result.message,
                isErrorFeedback = !result.success
            )
        }
        return result.success
    }

    fun loginAsDemoRole(role: UserRole): Boolean {
        val result = repository.loginWithDemoAccount(role)
        _uiState.update { state ->
            state.copy(
                snapshot = repository.getSnapshot(),
                currentUser = result.user ?: state.currentUser,
                feedbackMessage = result.message,
                isErrorFeedback = !result.success
            )
        }
        return result.success
    }

    fun registerUser(
        fullName: String,
        email: String,
        nipOrNuptk: String,
        requestedRole: UserRole
    ): Boolean {
        val result = repository.registerSelfServiceUser(
            fullName = fullName,
            email = email,
            nipOrNuptk = nipOrNuptk,
            requestedRole = requestedRole
        )
        _uiState.update { state ->
            state.copy(
                snapshot = repository.getSnapshot(),
                feedbackMessage = result.message,
                isErrorFeedback = !result.success
            )
        }
        return result.success
    }

    fun logout() {
        _uiState.update { state ->
            state.copy(
                currentUser = null,
                feedbackMessage = "Anda telah keluar dari sesi internal.",
                isErrorFeedback = false
            )
        }
    }

    /**
     * Mengunggah file fisik dari perangkat ke folder Google Drive sekolah,
     * lalu menyimpan tautan resminya pada catatan dokumen SIADS.
     */
    fun uploadAndSubmitDocument(
        title: String,
        categoryId: String,
        documentNumber: String,
        fileUri: Uri?
    ) {
        val actor = _uiState.value.currentUser
        if (actor == null) {
            _uiState.update {
                it.copy(
                    feedbackMessage = "Silakan masuk terlebih dahulu untuk mengunggah dokumen.",
                    isErrorFeedback = true
                )
            }
            return
        }
        if (title.isBlank()) {
            _uiState.update {
                it.copy(
                    feedbackMessage = "Judul dokumen tidak boleh kosong.",
                    isErrorFeedback = true
                )
            }
            return
        }

        val token = _uiState.value.driveAccessToken
        if (fileUri != null) {
            if (token.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        feedbackMessage = "Hubungkan akun Google Drive terlebih dahulu agar file dapat disimpan ke folder Drive sekolah.",
                        isErrorFeedback = true
                    )
                }
                return
            }

            val categoryTitle = CategoryType.fromId(categoryId).title
            _uiState.update { it.copy(isDriveUploading = true, feedbackMessage = null) }
            viewModelScope.launch {
                val uploadResult = googleDriveService.uploadFileToTargetFolder(
                    context = getApplication(),
                    accessToken = token,
                    fileUri = fileUri,
                    documentTitle = title,
                    categoryTitle = categoryTitle,
                    documentNumber = documentNumber
                )
                uploadResult.onSuccess { uploaded ->
                    repository.submitDocument(
                        title = title,
                        categoryId = categoryId,
                        documentNumber = documentNumber,
                        actor = actor,
                        fileUrlOrPath = uploaded.webViewLink
                    )
                    _uiState.update { state ->
                        state.copy(
                            isDriveUploading = false,
                            snapshot = repository.getSnapshot(),
                            feedbackMessage = "File \"${uploaded.fileName}\" berhasil disimpan ke folder Google Drive sekolah.",
                            isErrorFeedback = false
                        )
                    }
                    refreshDriveFolderFiles()
                }.onFailure { err ->
                    _uiState.update { state ->
                        state.copy(
                            isDriveUploading = false,
                            feedbackMessage = "Gagal unggah ke Google Drive: ${err.message}",
                            isErrorFeedback = true
                        )
                    }
                }
            }
        } else {
            repository.submitDocument(title, categoryId, documentNumber, actor, null)
            _uiState.update { state ->
                state.copy(
                    snapshot = repository.getSnapshot(),
                    feedbackMessage = "Catatan dokumen \"$title\" ditambahkan di mode Demo Preview (tanpa lampiran file Drive).",
                    isErrorFeedback = false
                )
            }
        }
    }

    fun reviewDocument(documentId: String, newStatus: VerificationStatus, notes: String) {
        val actor = _uiState.value.currentUser ?: return
        val ok = repository.reviewDocument(documentId, newStatus, notes, actor)
        _uiState.update { state ->
            state.copy(
                snapshot = repository.getSnapshot(),
                feedbackMessage = if (ok) {
                    "Status verifikasi diperbarui menjadi ${newStatus.label}."
                } else {
                    "Peran ${actor.role.badgeCode} tidak memiliki izin verifikasi."
                },
                isErrorFeedback = !ok
            )
        }
    }

    fun approveUser(targetUid: String) {
        val actor = _uiState.value.currentUser ?: return
        val ok = repository.approveUser(targetUid, actor)
        _uiState.update { state ->
            state.copy(
                snapshot = repository.getSnapshot(),
                feedbackMessage = if (ok) "Akun berhasil disetujui." else "Hanya ADMIN yang dapat menyetujui akun.",
                isErrorFeedback = !ok
            )
        }
    }

    fun changeUserRole(targetUid: String, newRole: UserRole) {
        val actor = _uiState.value.currentUser ?: return
        val ok = repository.changeUserRole(targetUid, newRole, actor)
        _uiState.update { state ->
            state.copy(
                snapshot = repository.getSnapshot(),
                feedbackMessage = if (ok) "Peran pengguna diperbarui menjadi ${newRole.badgeCode}." else "Hanya ADMIN yang dapat mengubah peran.",
                isErrorFeedback = !ok
            )
        }
    }

    fun setCategoryFilter(category: CategoryType?) {
        _uiState.update { it.copy(selectedCategoryFilter = category) }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null, isErrorFeedback = false) }
    }
}
