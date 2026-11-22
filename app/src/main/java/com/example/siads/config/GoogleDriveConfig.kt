package com.example.siads.config

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Konfigurasi resmi Google Drive tujuan penyimpanan seluruh unggahan file SIADS SD Negeri 1 Ngantru.
 */
object GoogleDriveConfig {
    const val TARGET_FOLDER_ID = "1rPq3eEgEZbO_uvFSEO7qEwWm4XFWoRVl"
    const val TARGET_FOLDER_URL =
        "https://drive.google.com/drive/folders/1rPq3eEgEZbO_uvFSEO7qEwWm4XFWoRVl?usp=sharing"

    const val DRIVE_SCOPE = "https://www.googleapis.com/auth/drive"
    const val DRIVE_UPLOAD_MULTIPART_URL =
        "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart&fields=id,name,mimeType,webViewLink,parents&supportsAllDrives=true"
    const val DRIVE_FILES_URL = "https://www.googleapis.com/drive/v3/files"

    fun fileViewUrl(fileId: String): String = "https://drive.google.com/file/d/$fileId/view"

    fun openTargetFolder(context: Context): Boolean = openUrl(context, TARGET_FOLDER_URL)

    fun openDriveFile(context: Context, url: String): Boolean {
        if (!url.startsWith("https://") && !url.startsWith("http://")) return false
        return openUrl(context, url)
    }

    private fun openUrl(context: Context, url: String): Boolean {
        return runCatching {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        }.getOrDefault(false)
    }
}
