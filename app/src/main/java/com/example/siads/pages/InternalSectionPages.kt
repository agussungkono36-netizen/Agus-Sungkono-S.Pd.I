package com.example.siads.pages

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.siads.components.DocumentItemCard
import com.example.siads.components.DocumentUploadDialog
import com.example.siads.components.GoogleDriveStorageCard
import com.example.siads.components.RoleBadge
import com.example.siads.components.VerificationStatusChip
import com.example.siads.components.rememberDriveAuthLauncher
import com.example.siads.config.GoogleDriveConfig
import com.example.siads.config.PwaManifestHelper
import com.example.siads.config.SchoolDomainConfig
import com.example.siads.hooks.SiadsUiState
import com.example.siads.layouts.InternalSidebarLayout
import com.example.siads.services.FirestoreSchemaContract
import com.example.siads.types.CategoryType
import com.example.siads.types.SiadsRoutes
import com.example.siads.types.UserRole
import com.example.siads.types.VerificationStatus
import com.example.ui.theme.StatusVerified
import com.google.android.gms.auth.api.identity.AuthorizationRequest

@Composable
fun DocumentsEvidencePage(
    uiState: SiadsUiState,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onSelectFilter: (CategoryType?) -> Unit,
    buildDriveAuthRequest: () -> AuthorizationRequest,
    onDriveTokenGranted: (String) -> Unit,
    onDriveAuthError: (String) -> Unit,
    onUploadDocument: (title: String, categoryId: String, docNumber: String, fileUri: Uri?) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val launchDriveAuth = rememberDriveAuthLauncher(
        buildAuthRequest = buildDriveAuthRequest,
        onTokenGranted = onDriveTokenGranted,
        onAuthError = onDriveAuthError
    )
    val selectedFilter = uiState.selectedCategoryFilter
    val filteredDocs = uiState.snapshot.documents.filter {
        selectedFilter == null || it.categoryId == selectedFilter.id
    }

    InternalSidebarLayout(
        title = "Dokumen & Bukti",
        currentRoute = SiadsRoutes.DOCUMENTS,
        currentUser = uiState.currentUser,
        onNavigate = onNavigate,
        onLogout = onLogout
    ) { _ ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier.widthIn(max = 940.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (uiState.feedbackMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (uiState.isErrorFeedback) {
                                MaterialTheme.colorScheme.errorContainer
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.feedbackMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (uiState.isErrorFeedback) {
                                    MaterialTheme.colorScheme.onErrorContainer
                                } else {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                },
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    GoogleDriveStorageCard(
                        isDriveConnected = uiState.isDriveConnected,
                        driveFileCount = uiState.driveFiles.size,
                        onConnectDrive = launchDriveAuth
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Arsip Dokumen (${filteredDocs.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = {
                                if (uiState.isAuthenticated) showAddDialog = true
                                else onNavigate(SiadsRoutes.LOGIN)
                            },
                            modifier = Modifier.testTag("docs_add_button")
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (uiState.isAuthenticated) "Unggah ke Drive" else "Login")
                        }
                    }

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { onSelectFilter(null) },
                            label = { Text("Semua") }
                        )
                        CategoryType.entries.forEach { cat ->
                            FilterChip(
                                selected = selectedFilter == cat,
                                onClick = { onSelectFilter(cat) },
                                label = { Text(cat.shortTitle) }
                            )
                        }
                    }
                }
            }

            if (uiState.driveFiles.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.widthIn(max = 940.dp).fillMaxWidth()) {
                        Text(
                            text = "File di Folder Google Drive Sekolah (${uiState.driveFiles.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                items(uiState.driveFiles, key = { it.id }) { driveFile ->
                    val context = LocalContext.current
                    Card(
                        onClick = { GoogleDriveConfig.openDriveFile(context, driveFile.webViewLink) },
                        modifier = Modifier.widthIn(max = 940.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, StatusVerified.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(driveFile.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(
                                    text = driveFile.mimeType.ifBlank { "Google Drive File" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "Buka",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = StatusVerified
                            )
                        }
                    }
                }
            }

            items(filteredDocs, key = { it.id }) { doc ->
                DocumentItemCard(document = doc, modifier = Modifier.widthIn(max = 940.dp))
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (showAddDialog) {
        DocumentUploadDialog(
            initialCategory = selectedFilter ?: CategoryType.PEMBELAJARAN,
            allowCategorySwitch = true,
            isDriveConnected = uiState.isDriveConnected,
            isUploading = uiState.isDriveUploading,
            onRequestDriveAuth = launchDriveAuth,
            onDismiss = { showAddDialog = false },
            onConfirmUpload = { title, catId, number, fileUri ->
                onUploadDocument(title, catId, number, fileUri)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun VerificationsPage(
    uiState: SiadsUiState,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onReviewDocument: (docId: String, status: VerificationStatus, notes: String) -> Unit
) {
    val canVerify = uiState.currentUser?.role == UserRole.KEPALA_SEKOLAH ||
        uiState.currentUser?.role == UserRole.ADMIN

    InternalSidebarLayout(
        title = "Verifikasi & Standar",
        currentRoute = SiadsRoutes.VERIFICATIONS,
        currentUser = uiState.currentUser,
        onNavigate = onNavigate,
        onLogout = onLogout
    ) { _ ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Column(modifier = Modifier.widthIn(max = 940.dp).fillMaxWidth()) {
                    Text(
                        text = "Riwayat & Antrean Verifikasi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (!canVerify) {
                        Text(
                            text = "Catatan: Aksi persetujuan verifikasi hanya aktif untuk peran KEPALA_SEKOLAH dan ADMIN.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(uiState.snapshot.verifications, key = { it.id }) { ver ->
                Card(
                    modifier = Modifier.widthIn(max = 940.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ver.targetType,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            VerificationStatusChip(status = ver.status)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(ver.targetTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Peninjau: ${ver.reviewerName} • ${ver.reviewedAtIso}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = ver.notes, style = MaterialTheme.typography.bodySmall)

                        if (canVerify && ver.status != VerificationStatus.DISETUJUI) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        onReviewDocument(
                                            ver.targetId,
                                            VerificationStatus.DISETUJUI,
                                            "Terverifikasi lengkap"
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusVerified),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Setujui")
                                }
                                OutlinedButton(
                                    onClick = {
                                        onReviewDocument(
                                            ver.targetId,
                                            VerificationStatus.PERLU_REVISI,
                                            "Perlu perbaikan berkas"
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Revisi")
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun ActivityLogsPage(
    uiState: SiadsUiState,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    InternalSidebarLayout(
        title = "Log Aktivitas",
        currentRoute = SiadsRoutes.ACTIVITY_LOGS,
        currentUser = uiState.currentUser,
        onNavigate = onNavigate,
        onLogout = onLogout
    ) { _ ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Column(modifier = Modifier.widthIn(max = 940.dp).fillMaxWidth()) {
                    Text(
                        text = "Jejak Audit Sistem (Koleksi activityLogs)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            items(uiState.snapshot.activityLogs, key = { it.id }) { log ->
                Card(
                    modifier = Modifier.widthIn(max = 940.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(log.action, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                RoleBadge(role = log.actorRole)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(log.detail, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${log.actorName} • Koleksi: ${log.targetCollection} • ${log.timestampIso}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun UserManagementPage(
    uiState: SiadsUiState,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onApproveUser: (uid: String) -> Unit,
    onChangeUserRole: (uid: String, newRole: UserRole) -> Unit
) {
    val isAdmin = uiState.currentUser?.role == UserRole.ADMIN

    InternalSidebarLayout(
        title = "Manajemen Pengguna",
        currentRoute = SiadsRoutes.USER_MANAGEMENT,
        currentUser = uiState.currentUser,
        onNavigate = onNavigate,
        onLogout = onLogout
    ) { _ ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Column(modifier = Modifier.widthIn(max = 940.dp).fillMaxWidth()) {
                    Text(
                        text = "Daftar Pengguna (Koleksi users)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pendaftar mandiri tidak otomatis menjadi ADMIN. Hanya ADMIN yang dapat menyetujui akun atau mengubah peran.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(uiState.snapshot.users, key = { it.uid }) { account ->
                Card(
                    modifier = Modifier.widthIn(max = 940.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(account.fullName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(account.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            RoleBadge(role = account.role)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (account.isApprovedByAdmin) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                            } else {
                                MaterialTheme.colorScheme.secondaryContainer
                            }
                        ) {
                            Text(
                                text = if (account.isApprovedByAdmin) "Status: Aktif & Disetujui" else "Status: Menunggu Persetujuan Admin",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        if (isAdmin) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!account.isApprovedByAdmin) {
                                    Button(onClick = { onApproveUser(account.uid) }) {
                                        Text("Setujui Akun")
                                    }
                                }
                                UserRole.entries.forEach { roleOption ->
                                    if (roleOption != account.role && account.uid != uiState.currentUser.uid) {
                                        OutlinedButton(
                                            onClick = { onChangeUserRole(account.uid, roleOption) }
                                        ) {
                                            Text("Jadikan ${roleOption.badgeCode}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.widthIn(max = 940.dp).fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Struktur Awal 8 Koleksi Firestore (Siap Produksi)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(FirestoreSchemaContract.blueprints, key = { it.collectionName }) { schema ->
                Card(
                    modifier = Modifier.widthIn(max = 940.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${schema.collectionName} (PK: ${schema.primaryKey})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Field: ${schema.requiredFields.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = schema.accessPolicySummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                val manifest = PwaManifestHelper.schoolManifest
                Card(
                    modifier = Modifier.widthIn(max = 940.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Konfigurasi PWA WebAppManifest (${SchoolDomainConfig.PRIMARY_DOMAIN})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Nama: ${manifest.name} (${manifest.shortName}) • Display: ${manifest.display.value}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Theme Color: ${manifest.themeColor} • Background: ${manifest.backgroundColor}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Ikon: ${manifest.icons.size} ukuran • Pintasan Kategori: ${manifest.shortcuts.size}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
