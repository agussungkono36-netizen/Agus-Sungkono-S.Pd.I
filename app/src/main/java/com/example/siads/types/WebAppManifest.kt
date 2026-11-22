package com.example.siads.types

/**
 * Representasi ikon pada W3C Web App Manifest untuk konfigurasi PWA sekolah.
 */
data class WebAppIcon(
    val src: String,
    val sizes: String,
    val type: String = "image/png",
    val purpose: String = "any"
)

/**
 * Representasi pintasan cepat (App Shortcuts) PWA untuk 5 kategori utama SIADS.
 */
data class WebAppShortcut(
    val name: String,
    val shortName: String,
    val description: String,
    val url: String,
    val icons: List<WebAppIcon> = emptyList()
)

enum class WebAppDisplayMode(val value: String) {
    STANDALONE("standalone"),
    MINIMAL_UI("minimal-ui"),
    FULLSCREEN("fullscreen"),
    BROWSER("browser")
}

/**
 * Kontrak antarmuka WebAppManifest sesuai spesifikasi W3C PWA.
 */
interface WebAppManifest {
    val name: String
    val shortName: String
    val description: String
    val startUrl: String
    val scope: String
    val display: WebAppDisplayMode
    val orientation: String
    val lang: String
    val themeColor: String
    val backgroundColor: String
    val categories: List<String>
    val icons: List<WebAppIcon>
    val shortcuts: List<WebAppShortcut>
}
