package com.example.siads.types

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CategoryLingkungan
import com.example.ui.theme.CategoryPembelajaran
import com.example.ui.theme.CategoryPengembanganDiri
import com.example.ui.theme.CategoryProfilSekolah
import com.example.ui.theme.CategoryTataKelola

/**
 * Empat peran resmi pada sistem SIADS SD Negeri 1 Ngantru.
 */
enum class UserRole(
    val label: String,
    val badgeCode: String,
    val summary: String,
    val allowSelfRegistration: Boolean
) {
    ADMIN(
        label = "Administrator",
        badgeCode = "ADMIN",
        summary = "Kelola akun pengguna, penetapan peran, konfigurasi sistem & audit log.",
        allowSelfRegistration = false // ATURAN KEAMANAN: Tidak boleh dipilih saat daftar mandiri
    ),
    KEPALA_SEKOLAH(
        label = "Kepala Sekolah",
        badgeCode = "KEPALA_SEKOLAH",
        summary = "Verifikasi dokumen akreditasi, tinjau standar mutu & pantau kinerja sekolah.",
        allowSelfRegistration = false
    ),
    GURU(
        label = "Guru",
        badgeCode = "GURU",
        summary = "Unggah perangkat pembelajaran, bukti pengembangan diri & eviden kelas.",
        allowSelfRegistration = true
    ),
    OPERATOR(
        label = "Operator Sekolah",
        badgeCode = "OPERATOR",
        summary = "Kelola profil sekolah, data guru, tata kelola administrasi & berkas sarpras.",
        allowSelfRegistration = true
    )
}

/**
 * Lima kategori utama di halaman Homepage SIADS.
 */
enum class CategoryType(
    val id: String,
    val title: String,
    val shortTitle: String,
    val subtitle: String,
    val accentColor: Color,
    val onAccentColor: Color,
    val standardCode: String
) {
    PENGEMBANGAN_DIRI(
        id = "pengembangan_diri",
        title = "PENGEMBANGAN DIRI",
        shortTitle = "Pengembangan Diri",
        subtitle = "Ekstrakurikuler, prestasi siswa, PKB guru & pelatihan kompetensi",
        accentColor = CategoryPengembanganDiri,
        onAccentColor = Color.White,
        standardCode = "STD-PD-01"
    ),
    LINGKUNGAN(
        id = "lingkungan",
        title = "LINGKUNGAN",
        shortTitle = "Lingkungan",
        subtitle = "Sekolah Adiwiyata, UKS, sanitasi, kebersihan & ruang terbuka hijau",
        accentColor = CategoryLingkungan,
        onAccentColor = Color.White,
        standardCode = "STD-LG-02"
    ),
    PEMBELAJARAN(
        id = "pembelajaran",
        title = "PEMBELAJARAN",
        shortTitle = "Pembelajaran",
        subtitle = "Kurikulum Merdeka, RPP/Modul Ajar, asesmen & supervisi kelas",
        accentColor = CategoryPembelajaran,
        onAccentColor = Color.White,
        standardCode = "STD-PB-03"
    ),
    TATA_KELOLA_ADMINISTRASI(
        id = "tata_kelola_administrasi",
        title = "TATA KELOLA DAN ADMINISTRASI",
        shortTitle = "Tata Kelola & Adm.",
        subtitle = "RKAS/BOS, surat-menyurat, inventaris barang & administrasi kepegawaian",
        accentColor = CategoryTataKelola,
        onAccentColor = Color(0xFF142407), // Kontras tinggi pada hijau lime
        standardCode = "STD-TK-04"
    ),
    PROFIL_SEKOLAH(
        id = "profil_sekolah",
        title = "PROFIL SEKOLAH",
        shortTitle = "Profil Sekolah",
        subtitle = "Identitas sekolah, NPSN, visi misi, struktur organisasi & data GTK",
        accentColor = CategoryProfilSekolah,
        onAccentColor = Color.White,
        standardCode = "STD-PS-05"
    );

    companion object {
        fun fromId(id: String?): CategoryType =
            entries.firstOrNull { it.id == id } ?: PENGEMBANGAN_DIRI
    }
}

enum class VerificationStatus(val label: String) {
    MENUNGGU("Menunggu Verifikasi"),
    DISETUJUI("Disetujui"),
    PERLU_REVISI("Perlu Revisi")
}

// ============================================================================
// 8 Koleksi Firestore SIADS SD NEGERI 1 NGANTRU
// ============================================================================

/** 1. Koleksi `users` */
data class UserProfile(
    val uid: String,
    val fullName: String,
    val email: String,
    val role: UserRole,
    val nipOrNuptk: String,
    val isActive: Boolean,
    val isApprovedByAdmin: Boolean,
    val createdAtIso: String,
    val isDemoAccount: Boolean = false
) {
    fun toFirestoreMap(): Map<String, Any> = mapOf(
        "uid" to uid,
        "fullName" to fullName,
        "email" to email,
        "role" to role.name,
        "nipOrNuptk" to nipOrNuptk,
        "isActive" to isActive,
        "isApprovedByAdmin" to isApprovedByAdmin,
        "createdAtIso" to createdAtIso
    )
}

/** 2. Koleksi `teachers` */
data class TeacherRecord(
    val id: String,
    val fullName: String,
    val nip: String,
    val position: String,
    val classAssigned: String,
    val certificationStatus: String,
    val phone: String,
    val isActive: Boolean
) {
    fun toFirestoreMap(): Map<String, Any> = mapOf(
        "id" to id,
        "fullName" to fullName,
        "nip" to nip,
        "position" to position,
        "classAssigned" to classAssigned,
        "certificationStatus" to certificationStatus,
        "phone" to phone,
        "isActive" to isActive
    )
}

/** 3. Koleksi `categories` */
data class CategoryItem(
    val id: String,
    val type: CategoryType,
    val description: String,
    val coordinatorName: String,
    val targetDocumentCount: Int,
    val sortOrder: Int
) {
    fun toFirestoreMap(): Map<String, Any> = mapOf(
        "id" to id,
        "code" to type.standardCode,
        "title" to type.title,
        "description" to description,
        "coordinatorName" to coordinatorName,
        "targetDocumentCount" to targetDocumentCount,
        "sortOrder" to sortOrder
    )
}

/** 4. Koleksi `documents` */
data class SchoolDocument(
    val id: String,
    val title: String,
    val categoryId: String,
    val standardId: String,
    val documentNumber: String,
    val schoolYear: String,
    val uploadedByUid: String,
    val uploadedByName: String,
    val fileUrlOrPath: String,
    val status: VerificationStatus,
    val updatedAtIso: String
) {
    fun toFirestoreMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "categoryId" to categoryId,
        "standardId" to standardId,
        "documentNumber" to documentNumber,
        "schoolYear" to schoolYear,
        "uploadedByUid" to uploadedByUid,
        "uploadedByName" to uploadedByName,
        "fileUrlOrPath" to fileUrlOrPath,
        "status" to status.name,
        "updatedAtIso" to updatedAtIso
    )
}

/** 5. Koleksi `accreditationStandards` */
data class AccreditationStandard(
    val id: String,
    val code: String,
    val title: String,
    val categoryId: String,
    val indicatorSummary: String,
    val weightScore: Int,
    val maxScore: Int
) {
    fun toFirestoreMap(): Map<String, Any> = mapOf(
        "id" to id,
        "code" to code,
        "title" to title,
        "categoryId" to categoryId,
        "indicatorSummary" to indicatorSummary,
        "weightScore" to weightScore,
        "maxScore" to maxScore
    )
}

/** 6. Koleksi `evidence` */
data class EvidenceItem(
    val id: String,
    val title: String,
    val categoryId: String,
    val documentId: String,
    val mediaType: String,
    val description: String,
    val capturedDateIso: String,
    val submittedByName: String,
    val verificationStatus: VerificationStatus
) {
    fun toFirestoreMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "categoryId" to categoryId,
        "documentId" to documentId,
        "mediaType" to mediaType,
        "description" to description,
        "capturedDateIso" to capturedDateIso,
        "submittedByName" to submittedByName,
        "verificationStatus" to verificationStatus.name
    )
}

/** 7. Koleksi `verifications` */
data class VerificationRecord(
    val id: String,
    val targetId: String,
    val targetType: String, // "DOCUMENT" atau "EVIDENCE"
    val targetTitle: String,
    val categoryId: String,
    val reviewerUid: String,
    val reviewerName: String,
    val status: VerificationStatus,
    val notes: String,
    val reviewedAtIso: String
) {
    fun toFirestoreMap(): Map<String, Any> = mapOf(
        "id" to id,
        "targetId" to targetId,
        "targetType" to targetType,
        "targetTitle" to targetTitle,
        "categoryId" to categoryId,
        "reviewerUid" to reviewerUid,
        "reviewerName" to reviewerName,
        "status" to status.name,
        "notes" to notes,
        "reviewedAtIso" to reviewedAtIso
    )
}

/** 8. Koleksi `activityLogs` */
data class ActivityLog(
    val id: String,
    val actorUid: String,
    val actorName: String,
    val actorRole: UserRole,
    val action: String,
    val targetCollection: String,
    val detail: String,
    val timestampIso: String
) {
    fun toFirestoreMap(): Map<String, Any> = mapOf(
        "id" to id,
        "actorUid" to actorUid,
        "actorName" to actorName,
        "actorRole" to actorRole.name,
        "action" to action,
        "targetCollection" to targetCollection,
        "detail" to detail,
        "timestampIso" to timestampIso
    )
}

/**
 * Rute navigasi terpusat aplikasi SIADS.
 */
object SiadsRoutes {
    const val HOME = "home"
    const val CATEGORY_PREFIX = "category"
    const val CATEGORY_PATTERN = "category/{categoryId}"
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val DOCUMENTS = "documents"
    const val VERIFICATIONS = "verifications"
    const val ACTIVITY_LOGS = "activity_logs"
    const val USER_MANAGEMENT = "user_management"

    fun categoryRoute(categoryId: String): String = "$CATEGORY_PREFIX/$categoryId"
}
