package com.example.siads.config

import com.example.siads.types.CategoryType
import com.example.siads.types.WebAppDisplayMode
import com.example.siads.types.WebAppIcon
import com.example.siads.types.WebAppManifest
import com.example.siads.types.WebAppShortcut

/**
 * Implementasi konkret WebAppManifest untuk SIADS SD Negeri 1 Ngantru.
 */
data class SiadsWebAppManifest(
    override val name: String,
    override val shortName: String,
    override val description: String,
    override val startUrl: String,
    override val scope: String,
    override val display: WebAppDisplayMode,
    override val orientation: String,
    override val lang: String,
    override val themeColor: String,
    override val backgroundColor: String,
    override val categories: List<String>,
    override val icons: List<WebAppIcon>,
    override val shortcuts: List<WebAppShortcut>
) : WebAppManifest

/**
 * Helper untuk mendefinisikan konfigurasi PWA (ikon, warna tema, pintasan kategori,
 * serta serialisasi JSON `manifest.webmanifest`) bagi domain sdnegeri1ngantru.schoolpro.id.
 */
object PwaManifestHelper {

    const val THEME_COLOR_HEX = "#102A43"       // Selaras dengan NavyPrimary di Color.kt
    const val BACKGROUND_COLOR_HEX = "#F8FAFC"  // Selaras dengan SurfaceLight di Color.kt

    val defaultIcons: List<WebAppIcon> = listOf(
        WebAppIcon(
            src = "${SchoolDomainConfig.BASE_URL}/icons/icon-192x192.png",
            sizes = "192x192",
            type = "image/png",
            purpose = "any"
        ),
        WebAppIcon(
            src = "${SchoolDomainConfig.BASE_URL}/icons/icon-512x512.png",
            sizes = "512x512",
            type = "image/png",
            purpose = "any"
        ),
        WebAppIcon(
            src = "${SchoolDomainConfig.BASE_URL}/icons/icon-maskable-512x512.png",
            sizes = "512x512",
            type = "image/png",
            purpose = "maskable"
        )
    )

    val categoryShortcuts: List<WebAppShortcut> = CategoryType.entries.map { category ->
        WebAppShortcut(
            name = category.title,
            shortName = category.shortTitle,
            description = category.subtitle,
            url = "/kategori/${category.id}",
            icons = listOf(
                WebAppIcon(
                    src = "${SchoolDomainConfig.BASE_URL}/icons/icon-192x192.png",
                    sizes = "192x192",
                    type = "image/png"
                )
            )
        )
    }

    val schoolManifest: WebAppManifest = SiadsWebAppManifest(
        name = "SIADS SD Negeri 1 Ngantru",
        shortName = "SIADS SDN1",
        description = "Sistem Informasi Akreditasi & Dokumentasi Sekolah SD Negeri 1 Ngantru",
        startUrl = "/",
        scope = "/",
        display = WebAppDisplayMode.STANDALONE,
        orientation = "any",
        lang = "id-ID",
        themeColor = THEME_COLOR_HEX,
        backgroundColor = BACKGROUND_COLOR_HEX,
        categories = listOf("education", "productivity"),
        icons = defaultIcons,
        shortcuts = categoryShortcuts
    )

    /**
     * Menghasilkan string JSON standar W3C `manifest.webmanifest` dari konfigurasi manifest sekolah.
     */
    fun toManifestJson(manifest: WebAppManifest = schoolManifest): String {
        val iconsJson = manifest.icons.joinToString(",\n    ") { icon ->
            """{"src": "${escapeJson(icon.src)}", "sizes": "${escapeJson(icon.sizes)}", "type": "${escapeJson(icon.type)}", "purpose": "${escapeJson(icon.purpose)}"}"""
        }
        val categoriesJson = manifest.categories.joinToString(", ") { "\"${escapeJson(it)}\"" }
        val shortcutsJson = manifest.shortcuts.joinToString(",\n    ") { sc ->
            """{"name": "${escapeJson(sc.name)}", "short_name": "${escapeJson(sc.shortName)}", "description": "${escapeJson(sc.description)}", "url": "${escapeJson(sc.url)}"}"""
        }

        return buildString {
            appendLine("{")
            appendLine("""  "name": "${escapeJson(manifest.name)}",""")
            appendLine("""  "short_name": "${escapeJson(manifest.shortName)}",""")
            appendLine("""  "description": "${escapeJson(manifest.description)}",""")
            appendLine("""  "start_url": "${escapeJson(manifest.startUrl)}",""")
            appendLine("""  "scope": "${escapeJson(manifest.scope)}",""")
            appendLine("""  "display": "${escapeJson(manifest.display.value)}",""")
            appendLine("""  "orientation": "${escapeJson(manifest.orientation)}",""")
            appendLine("""  "lang": "${escapeJson(manifest.lang)}",""")
            appendLine("""  "theme_color": "${escapeJson(manifest.themeColor)}",""")
            appendLine("""  "background_color": "${escapeJson(manifest.backgroundColor)}",""")
            appendLine("""  "categories": [$categoriesJson],""")
            appendLine("  \"icons\": [\n    $iconsJson\n  ],")
            appendLine("  \"shortcuts\": [\n    $shortcutsJson\n  ]")
            append("}")
        }
    }

    private fun escapeJson(value: String): String =
        value.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
}
