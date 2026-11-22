package com.example.siads.components

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.siads.config.GoogleDriveConfig
import com.example.siads.types.CategoryType
import com.example.ui.theme.StatusVerified
import com.example.ui.theme.StatusVerifiedContainer
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException

@Composable
fun rememberDriveAuthLauncher(
    buildAuthRequest: () -> AuthorizationRequest,
    onTokenGranted: (String) -> Unit,
    onAuthError: (String) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val resolutionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK && activityResult.data != null) {
            try {
                val authResult = Identity.getAuthorizationClient(context)
                    .getAuthorizationResultFromIntent(activityResult.data)
                val token = authResult.accessToken
                if (!token.isNullOrBlank()) {
                    onTokenGranted(token)
                } else {
                    onAuthError("Token akses Google Drive tidak diterima.")
                }
            } catch (e: ApiException) {
                onAuthError("Otorisasi Google Drive gagal (${e.statusCode}): ${e.localizedMessage ?: "Unknown"}")
            }
        } else {
            onAuthError("Izin akses Google Drive dibatalkan oleh pengguna.")
        }
    }

    return remember(context, buildAuthRequest) {
        {
            Identity.getAuthorizationClient(context)
                .authorize(buildAuthRequest())
                .addOnSuccessListener { result ->
                    if (result.hasResolution() && result.pendingIntent != null) {
                        val req = IntentSenderRequest.Builder(result.pendingIntent!!.intentSender).build()
                        resolutionLauncher.launch(req)
                    } else {
                        val token = result.accessToken
                        if (!token.isNullOrBlank()) {
                            onTokenGranted(token)
                        } else {
                            onAuthError("Tidak dapat memperoleh token Google Drive.")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    onAuthError("Gagal menghubungi layanan Google Drive OAuth: ${e.localizedMessage ?: "Periksa koneksi/Play Services"}")
                }
        }
    }
}

@Composable
fun GoogleDriveStorageCard(
    isDriveConnected: Boolean,
    driveFileCount: Int,
    onConnectDrive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDriveConnected) {
                StatusVerifiedContainer.copy(alpha = 0.55f)
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            }
        ),
        border = BorderStroke(
            1.dp,
            if (isDriveConnected) StatusVerified.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = if (isDriveConnected) Icons.Filled.CloudDone else Icons.Filled.CloudUpload,
                        contentDescription = null,
                        tint = if (isDriveConnected) StatusVerified else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isDriveConnected) {
                            "Google Drive Terhubung ($driveFileCount file di folder)"
                        } else {
                            "Penyimpanan Google Drive Sekolah"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isDriveConnected) StatusVerified else MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = if (isDriveConnected) "AKTIF" else "SIAP",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = "Folder Tujuan: ${GoogleDriveConfig.TARGET_FOLDER_ID}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isDriveConnected) {
                    Button(
                        onClick = onConnectDrive,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("connect_google_drive_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Hubungkan Drive", maxLines = 1)
                    }
                }
                OutlinedButton(
                    onClick = { GoogleDriveConfig.openTargetFolder(context) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("open_drive_folder_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Buka Folder Drive", maxLines = 1)
                }
            }
        }
    }
}

@Composable
fun DocumentUploadDialog(
    initialCategory: CategoryType,
    allowCategorySwitch: Boolean,
    isDriveConnected: Boolean,
    isUploading: Boolean,
    onRequestDriveAuth: () -> Unit,
    onDismiss: () -> Unit,
    onConfirmUpload: (title: String, categoryId: String, docNumber: String, fileUri: Uri?) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var docNumber by remember { mutableStateOf("") }
    var pickedCategory by remember { mutableStateOf(initialCategory) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedFileUri = uri
            selectedFileName = resolveUriDisplayName(context, uri)
            if (title.isBlank() && selectedFileName != null) {
                title = selectedFileName!!.substringBeforeLast('.')
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isUploading) onDismiss() },
        title = {
            Text(
                text = if (allowCategorySwitch) "Unggah Dokumen ke Google Drive"
                else "Unggah ${initialCategory.shortTitle} ke Drive"
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Dokumen") },
                    singleLine = true,
                    enabled = !isUploading,
                    modifier = Modifier.fillMaxWidth().testTag("upload_title_input")
                )
                OutlinedTextField(
                    value = docNumber,
                    onValueChange = { docNumber = it },
                    label = { Text("Nomor Surat / Dokumen (Opsional)") },
                    singleLine = true,
                    enabled = !isUploading,
                    modifier = Modifier.fillMaxWidth()
                )

                if (allowCategorySwitch) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CategoryType.entries.forEach { cat ->
                            FilterChip(
                                selected = pickedCategory == cat,
                                onClick = { if (!isUploading) pickedCategory = cat },
                                label = { Text(cat.shortTitle) }
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
                    enabled = !isUploading,
                    modifier = Modifier.fillMaxWidth().testTag("pick_file_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.AttachFile, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedFileName ?: "Pilih File dari Perangkat (PDF/Foto/Doc)",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (selectedFileUri != null && !isDriveConnected) {
                    Button(
                        onClick = onRequestDriveAuth,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_connect_drive_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Otorisasi Google Drive untuk Mengunggah")
                    }
                }

                Text(
                    text = if (selectedFileUri != null && isDriveConnected) {
                        "File akan diunggah langsung ke Google Drive folder ${GoogleDriveConfig.TARGET_FOLDER_ID}."
                    } else if (selectedFileUri != null) {
                        "File siap; hubungkan Google Drive terlebih dahulu sebelum mengunggah."
                    } else {
                        "Pilih file agar diunggah ke Google Drive, atau simpan sebagai catatan dokumen."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isUploading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Text("Mengunggah file ke Google Drive...", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmUpload(title, pickedCategory.id, docNumber, selectedFileUri)
                },
                enabled = !isUploading && title.isNotBlank() && (selectedFileUri == null || isDriveConnected),
                modifier = Modifier.testTag("confirm_upload_button")
            ) {
                Text(if (selectedFileUri != null) "Unggah ke Drive" else "Simpan Catatan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isUploading) {
                Text("Batal")
            }
        }
    )
}

private fun resolveUriDisplayName(context: Context, uri: Uri): String? {
    return runCatching {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) cursor.getString(idx) else null
                } else null
            }
    }.getOrNull() ?: uri.lastPathSegment
}
