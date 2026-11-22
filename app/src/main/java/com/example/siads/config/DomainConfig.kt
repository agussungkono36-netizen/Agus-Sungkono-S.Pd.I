package com.example.siads.config

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.siads.types.CategoryType
import com.example.siads.types.SiadsRoutes

/**
 * Konfigurasi domain resmi sekolah untuk integrasi Portal Web & Android App Links.
 */
object SchoolDomainConfig {
    const val PRIMARY_DOMAIN = "sdnegeri1ngantru.schoolpro.id"
    const val BASE_URL = "https://$PRIMARY_DOMAIN"
    const val ASSET_LINKS_PATH = "$BASE_URL/.well-known/assetlinks.json"

    const val URL_HOME = "$BASE_URL/"
    const val URL_LOGIN = "$BASE_URL/login"
    const val URL_DASHBOARD = "$BASE_URL/dashboard"
    const val URL_DOCUMENTS = "$BASE_URL/dokumen"
    const val URL_VERIFICATIONS = "$BASE_URL/verifikasi"

    fun categoryUrl(categoryId: String): String = "$BASE_URL/kategori/$categoryId"

    /**
     * Memetakan URL atau URI dari domain sdnegeri1ngantru.schoolpro.id ke rute internal NavHost.
     */
    fun resolveRouteFromUri(uri: Uri?): String? {
        if (uri == null) return null
        val host = uri.host?.lowercase() ?: return null
        if (host != PRIMARY_DOMAIN) return null

        val segments = uri.pathSegments.map { it.lowercase() }
        if (segments.isEmpty()) return SiadsRoutes.HOME

        return when (segments.first()) {
            "login", "masuk" -> SiadsRoutes.LOGIN
            "dashboard" -> SiadsRoutes.DASHBOARD
            "dokumen", "documents" -> SiadsRoutes.DOCUMENTS
            "verifikasi", "verifications" -> SiadsRoutes.VERIFICATIONS
            "log", "aktivitas" -> SiadsRoutes.ACTIVITY_LOGS
            "pengguna", "users" -> SiadsRoutes.USER_MANAGEMENT
            "kategori", "category" -> {
                val categoryId = segments.getOrNull(1)
                if (categoryId != null) {
                    val matched = CategoryType.fromId(categoryId)
                    SiadsRoutes.categoryRoute(matched.id)
                } else {
                    SiadsRoutes.HOME
                }
            }
            else -> SiadsRoutes.HOME
        }
    }

    /**
     * Membuka URL portal web resmi sdnegeri1ngantru.schoolpro.id di browser perangkat.
     */
    fun openWebPortal(context: Context, url: String = BASE_URL): Boolean {
        return runCatching {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        }.getOrDefault(false)
    }
}
