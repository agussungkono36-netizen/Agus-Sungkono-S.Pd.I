package com.example

import com.example.siads.config.DataEnvironmentMode
import com.example.siads.config.FirestoreCollections
import com.example.siads.services.DemoPreviewData
import com.example.siads.services.DemoSiadsRepository
import com.example.siads.types.CategoryType
import com.example.siads.types.UserRole
import com.example.siads.types.VerificationStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `demo preview data is isolated from production`() {
        assertEquals(DataEnvironmentMode.DEMO_PREVIEW, DemoPreviewData.mode)
        assertTrue(DemoPreviewData.demoUsers.all { it.isDemoAccount })
    }

    @Test
    fun `firestore schema covers all 8 required collections`() {
        assertEquals(8, FirestoreCollections.allCollections.size)
        assertTrue(
            FirestoreCollections.allCollections.containsAll(
                listOf(
                    "users",
                    "teachers",
                    "categories",
                    "documents",
                    "accreditationStandards",
                    "evidence",
                    "verifications",
                    "activityLogs"
                )
            )
        )
    }

    @Test
    fun `self registration never grants ADMIN role`() {
        val repo = DemoSiadsRepository()
        val result = repo.registerSelfServiceUser(
            fullName = "Pendaftar Uji",
            email = "uji@sdn1ngantru.sch.id",
            nipOrNuptk = "1234567890",
            requestedRole = UserRole.ADMIN
        )
        assertTrue(result.success)
        val created = repo.getSnapshot().users.first { it.email == "uji@sdn1ngantru.sch.id" }
        assertNotEquals(UserRole.ADMIN, created.role)
        assertEquals(UserRole.GURU, created.role)
        assertFalse(created.isApprovedByAdmin)
    }

    @Test
    fun `five main categories and role verification workflow work in demo repository`() {
        assertEquals(5, CategoryType.entries.size)
        val repo = DemoSiadsRepository()
        val guru = repo.loginWithDemoAccount(UserRole.GURU).user!!
        val kepsek = repo.loginWithDemoAccount(UserRole.KEPALA_SEKOLAH).user!!
        val admin = repo.loginWithDemoAccount(UserRole.ADMIN).user!!

        val submitted = repo.submitDocument(
            title = "Perangkat Ajar Uji",
            categoryId = CategoryType.PEMBELAJARAN.id,
            documentNumber = "421.2/999/2026",
            actor = guru
        )

        // Guru tidak boleh memverifikasi dokumen
        assertFalse(repo.reviewDocument(submitted.id, VerificationStatus.DISETUJUI, "Coba", guru))
        // Kepala Sekolah boleh memverifikasi dokumen
        assertTrue(repo.reviewDocument(submitted.id, VerificationStatus.DISETUJUI, "Sah", kepsek))
        // Admin boleh menyetujui pengguna baru
        repo.registerSelfServiceUser("Guru Baru", "baruguru@sdn1ngantru.sch.id", "", UserRole.GURU)
        val pending = repo.getSnapshot().users.first { it.email == "baruguru@sdn1ngantru.sch.id" }
        assertFalse(repo.approveUser(pending.uid, guru))
        assertTrue(repo.approveUser(pending.uid, admin))
    }

    @Test
    fun `navigation routes cover homepage, 5 categories, login, and dashboard`() {
        assertEquals("home", com.example.siads.types.SiadsRoutes.HOME)
        assertEquals("login", com.example.siads.types.SiadsRoutes.LOGIN)
        assertEquals("dashboard", com.example.siads.types.SiadsRoutes.DASHBOARD)
        assertEquals("category/{categoryId}", com.example.siads.types.SiadsRoutes.CATEGORY_PATTERN)
        CategoryType.entries.forEach { category ->
            assertEquals("category/${category.id}", com.example.siads.types.SiadsRoutes.categoryRoute(category.id))
            assertEquals(category, CategoryType.fromId(category.id))
        }
    }

    @Test
    fun `school domain config points to sdnegeri1ngantru schoolpro id`() {
        assertEquals("sdnegeri1ngantru.schoolpro.id", com.example.siads.config.SchoolDomainConfig.PRIMARY_DOMAIN)
        assertEquals("https://sdnegeri1ngantru.schoolpro.id", com.example.siads.config.SchoolDomainConfig.BASE_URL)
        assertEquals(
            "https://sdnegeri1ngantru.schoolpro.id/kategori/pembelajaran",
            com.example.siads.config.SchoolDomainConfig.categoryUrl(CategoryType.PEMBELAJARAN.id)
        )
    }

    @Test
    fun `pwa manifest helper builds valid school WebAppManifest and json`() {
        val manifest: com.example.siads.types.WebAppManifest =
            com.example.siads.config.PwaManifestHelper.schoolManifest
        assertEquals("SIADS SD Negeri 1 Ngantru", manifest.name)
        assertEquals("#102A43", manifest.themeColor)
        assertEquals("#F8FAFC", manifest.backgroundColor)
        assertEquals(3, manifest.icons.size)
        assertEquals(5, manifest.shortcuts.size)

        val json = com.example.siads.config.PwaManifestHelper.toManifestJson(manifest)
        assertTrue(json.contains("\"theme_color\": \"#102A43\""))
        assertTrue(json.contains("\"background_color\": \"#F8FAFC\""))
        assertTrue(json.contains("sdnegeri1ngantru.schoolpro.id/icons/icon-512x512.png"))
        assertTrue(json.contains("\"url\": \"/kategori/pembelajaran\""))
    }

    @Test
    fun `google drive config targets requested school folder and stores drive links`() {
        assertEquals(
            "1rPq3eEgEZbO_uvFSEO7qEwWm4XFWoRVl",
            com.example.siads.config.GoogleDriveConfig.TARGET_FOLDER_ID
        )
        assertEquals(
            "https://www.googleapis.com/auth/drive",
            com.example.siads.config.GoogleDriveConfig.DRIVE_SCOPE
        )
        val repo = DemoSiadsRepository()
        val guru = repo.loginWithDemoAccount(UserRole.GURU).user!!
        val uploadedDoc = repo.submitDocument(
            title = "RPP Drive",
            categoryId = CategoryType.PEMBELAJARAN.id,
            documentNumber = "421.2/100/2026",
            actor = guru,
            fileUrlOrPath = com.example.siads.config.GoogleDriveConfig.fileViewUrl("abc123")
        )
        assertEquals("https://drive.google.com/file/d/abc123/view", uploadedDoc.fileUrlOrPath)
    }
}
