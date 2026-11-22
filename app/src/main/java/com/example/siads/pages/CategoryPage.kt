package com.example.siads.pages

import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.siads.components.DocumentItemCard
import com.example.siads.components.DocumentUploadDialog
import com.example.siads.components.GoogleDriveStorageCard
import com.example.siads.components.VerificationStatusChip
import com.example.siads.components.categoryIcon
import com.example.siads.components.rememberDriveAuthLauncher
import com.example.siads.hooks.SiadsUiState
import com.example.siads.layouts.InternalSidebarLayout
import com.example.siads.types.CategoryType
import com.example.siads.types.SiadsRoutes
import com.google.android.gms.auth.api.identity.AuthorizationRequest

@Composable
fun CategoryPage(
    category: CategoryType,
    uiState: SiadsUiState,
    onNavigate: (String) -> Unit,
    onBackHome: () -> Unit,
    onLogout: () -> Unit,
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

    val categoryMeta = uiState.snapshot.categories.firstOrNull { it.type == category }
    val standards = uiState.snapshot.standards.filter { it.categoryId == category.id }
    val documents = uiState.snapshot.documents.filter { it.categoryId == category.id }
    val evidence = uiState.snapshot.evidence.filter { it.categoryId == category.id }

    InternalSidebarLayout(
        title = category.shortTitle,
        currentRoute = SiadsRoutes.categoryRoute(category.id),
        currentUser = uiState.currentUser,
        onNavigate = onNavigate,
        onLogout = onLogout
    ) { _ ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Column(modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth()) {
                    // Header Kategori berwarna khas
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = category.accentColor,
                        contentColor = category.onAccentColor
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = category.onAccentColor.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = category.standardCode,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = category.onAccentColor,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Icon(
                                    imageVector = categoryIcon(category),
                                    contentDescription = category.title,
                                    tint = category.onAccentColor,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = category.onAccentColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = categoryMeta?.description ?: category.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = category.onAccentColor.copy(alpha = 0.92f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            val targetCount = (categoryMeta?.targetDocumentCount ?: 10).coerceAtLeast(1)
                            val progress = (documents.size.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f)
                            Text(
                                text = "Koordinator: ${categoryMeta?.coordinatorName ?: "Tim Akreditasi"} • ${documents.size}/$targetCount Dokumen",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = category.onAccentColor
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(8.dp),
                                color = category.onAccentColor,
                                trackColor = category.onAccentColor.copy(alpha = 0.25f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.feedbackMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (uiState.isErrorFeedback) {
                                MaterialTheme.colorScheme.errorContainer
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
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

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBackHome,
                            modifier = Modifier.height(48.dp).testTag("category_back_home"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Beranda")
                        }

                        Button(
                            onClick = {
                                if (uiState.isAuthenticated) {
                                    showAddDialog = true
                                } else {
                                    onNavigate(SiadsRoutes.LOGIN)
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp).testTag("category_add_doc"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isAuthenticated) "Unggah ke Drive" else "Login untuk Unggah",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (standards.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth()) {
                        Text(
                            text = "Standar Akreditasi Terkait",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                items(standards, key = { it.id }) { std ->
                    Card(
                        modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Assessment,
                                contentDescription = null,
                                tint = category.accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${std.code} — ${std.title}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = std.indicatorSummary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${std.weightScore}/${std.maxScore}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth()) {
                    Text(
                        text = "Dokumen Kategori (${documents.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            if (documents.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Belum ada dokumen pada kategori ini.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(documents, key = { it.id }) { doc ->
                    DocumentItemCard(
                        document = doc,
                        modifier = Modifier.widthIn(max = 900.dp)
                    )
                }
            }

            if (evidence.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth()) {
                        Text(
                            text = "Bukti Fisik / Evidence (${evidence.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                items(evidence, key = { it.id }) { ev ->
                    Card(
                        modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhotoLibrary,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ev.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${ev.mediaType} • ${ev.capturedDateIso} • ${ev.submittedByName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = ev.description,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            VerificationStatusChip(status = ev.verificationStatus)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (showAddDialog) {
        DocumentUploadDialog(
            initialCategory = category,
            allowCategorySwitch = false,
            isDriveConnected = uiState.isDriveConnected,
            isUploading = uiState.isDriveUploading,
            onRequestDriveAuth = launchDriveAuth,
            onDismiss = { showAddDialog = false },
            onConfirmUpload = { docTitle, catId, docNumber, fileUri ->
                onUploadDocument(docTitle, catId, docNumber, fileUri)
                showAddDialog = false
            }
        )
    }
}
