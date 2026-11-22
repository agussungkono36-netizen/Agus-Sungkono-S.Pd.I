package com.example.siads.services

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.siads.config.GoogleDriveConfig
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class DriveUploadResult(
    val fileId: String,
    val fileName: String,
    val mimeType: String,
    val webViewLink: String,
    val folderId: String
)

data class DriveFileSummary(
    val id: String,
    val name: String,
    val mimeType: String,
    val webViewLink: String,
    val modifiedTime: String
)

/**
 * Layanan integrasi Google Drive REST API v3 untuk menyimpan seluruh unggahan file
 * SIADS ke folder tujuan (1rPq3eEgEZbO_uvFSEO7qEwWm4XFWoRVl).
 */
class GoogleDriveService(
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
) {

    fun buildAuthorizationRequest(): AuthorizationRequest =
        AuthorizationRequest.builder()
            .setRequestedScopes(listOf(Scope(GoogleDriveConfig.DRIVE_SCOPE)))
            .build()

    suspend fun uploadFileToTargetFolder(
        context: Context,
        accessToken: String,
        fileUri: Uri,
        documentTitle: String,
        categoryTitle: String,
        documentNumber: String
    ): Result<DriveUploadResult> = withContext(Dispatchers.IO) {
        runCatching {
            val resolver = context.contentResolver
            val resolvedMime = resolver.getType(fileUri) ?: "application/octet-stream"
            val originalName = queryDisplayName(context, fileUri) ?: "${documentTitle.trim()}.bin"
            val uploadFileName = buildDriveFileName(documentTitle, originalName)

            val fileBytes = resolver.openInputStream(fileUri)?.use { it.readBytes() }
                ?: error("File tidak dapat dibaca dari perangkat.")

            if (fileBytes.isEmpty()) {
                error("File yang dipilih kosong (0 byte).")
            }

            val metadataJson = JSONObject().apply {
                put("name", uploadFileName)
                put("parents", JSONArray().put(GoogleDriveConfig.TARGET_FOLDER_ID))
                put(
                    "description",
                    "SIADS SD Negeri 1 Ngantru | Kategori: $categoryTitle | No: ${documentNumber.ifBlank { "-" }}"
                )
            }.toString()

            val multipartBody = MultipartBody.Builder()
                .setType("multipart/related".toMediaType())
                .addPart(
                    metadataJson.toRequestBody("application/json; charset=UTF-8".toMediaType())
                )
                .addPart(
                    fileBytes.toRequestBody(resolvedMime.toMediaType())
                )
                .build()

            val request = Request.Builder()
                .url(GoogleDriveConfig.DRIVE_UPLOAD_MULTIPART_URL)
                .addHeader("Authorization", "Bearer $accessToken")
                .post(multipartBody)
                .build()

            httpClient.newCall(request).execute().use { response ->
                val bodyText = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    val errorMsg = parseDriveErrorMessage(bodyText, response.code)
                    error(errorMsg)
                }
                val json = JSONObject(bodyText)
                val id = json.getString("id")
                val name = json.optString("name", uploadFileName)
                val mime = json.optString("mimeType", resolvedMime)
                val link = json.optString("webViewLink", GoogleDriveConfig.fileViewUrl(id))
                DriveUploadResult(
                    fileId = id,
                    fileName = name,
                    mimeType = mime,
                    webViewLink = link,
                    folderId = GoogleDriveConfig.TARGET_FOLDER_ID
                )
            }
        }
    }

    suspend fun listFilesInTargetFolder(accessToken: String): Result<List<DriveFileSummary>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val rawQuery =
                    "'${GoogleDriveConfig.TARGET_FOLDER_ID}' in parents and trashed = false"
                val encodedQuery = URLEncoder.encode(rawQuery, "UTF-8")
                val fields = URLEncoder.encode(
                    "files(id,name,mimeType,webViewLink,modifiedTime)",
                    "UTF-8"
                )
                val url = "${GoogleDriveConfig.DRIVE_FILES_URL}" +
                    "?q=$encodedQuery&fields=$fields&orderBy=modifiedTime%20desc" +
                    "&pageSize=30&supportsAllDrives=true&includeItemsFromAllDrives=true"

                val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .get()
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    val bodyText = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        error(parseDriveErrorMessage(bodyText, response.code))
                    }
                    val json = JSONObject(bodyText)
                    val array = json.optJSONArray("files") ?: JSONArray()
                    buildList {
                        for (i in 0 until array.length()) {
                            val item = array.getJSONObject(i)
                            val id = item.getString("id")
                            add(
                                DriveFileSummary(
                                    id = id,
                                    name = item.optString("name", "Dokumen"),
                                    mimeType = item.optString("mimeType", ""),
                                    webViewLink = item.optString(
                                        "webViewLink",
                                        GoogleDriveConfig.fileViewUrl(id)
                                    ),
                                    modifiedTime = item.optString("modifiedTime", "")
                                )
                            )
                        }
                    }
                }
            }
        }

    private fun queryDisplayName(context: Context, uri: Uri): String? {
        return runCatching {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (idx >= 0) cursor.getString(idx) else null
                    } else null
                }
        }.getOrNull()
    }

    private fun buildDriveFileName(documentTitle: String, originalName: String): String {
        val cleanTitle = documentTitle.trim()
        if (cleanTitle.isBlank()) return originalName
        val extIndex = originalName.lastIndexOf('.')
        val ext = if (extIndex > 0 && extIndex < originalName.length - 1) {
            originalName.substring(extIndex)
        } else ""
        return if (cleanTitle.endsWith(ext, ignoreCase = true)) cleanTitle else "$cleanTitle$ext"
    }

    private fun parseDriveErrorMessage(bodyText: String, code: Int): String {
        return runCatching {
            val errObj = JSONObject(bodyText).optJSONObject("error")
            val msg = errObj?.optString("message").orEmpty()
            if (msg.isNotBlank()) "Google Drive ($code): $msg" else "Google Drive error HTTP $code"
        }.getOrDefault("Google Drive error HTTP $code")
    }
}
