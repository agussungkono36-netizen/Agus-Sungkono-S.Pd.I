package com.example.siads.services

import com.example.siads.config.DataEnvironmentMode
import com.example.siads.config.FirestoreCollections
import com.example.siads.types.AccreditationStandard
import com.example.siads.types.ActivityLog
import com.example.siads.types.CategoryItem
import com.example.siads.types.CategoryType
import com.example.siads.types.EvidenceItem
import com.example.siads.types.SchoolDocument
import com.example.siads.types.TeacherRecord
import com.example.siads.types.UserProfile
import com.example.siads.types.UserRole
import com.example.siads.types.VerificationRecord
import com.example.siads.types.VerificationStatus
import java.util.UUID

data class AuthResult(
    val success: Boolean,
    val user: UserProfile? = null,
    val message: String
)

data class SiadsSnapshot(
    val mode: DataEnvironmentMode,
    val users: List<UserProfile>,
    val teachers: List<TeacherRecord>,
    val categories: List<CategoryItem>,
    val standards: List<AccreditationStandard>,
    val documents: List<SchoolDocument>,
    val evidence: List<EvidenceItem>,
    val verifications: List<VerificationRecord>,
    val activityLogs: List<ActivityLog>
)

/**
 * Kontrak repositori data SIADS.
 * Tahap pertama menggunakan DemoSiadsRepository (terisolasi dari produksi).
 * Tahap berikutnya dapat ditukar dengan FirestoreSiadsRepository setelah google-services.json aktif.
 */
interface SiadsRepository {
    fun getSnapshot(): SiadsSnapshot
    fun loginWithEmail(email: String, password: String): AuthResult
    fun loginWithDemoAccount(role: UserRole): AuthResult
    fun registerSelfServiceUser(
        fullName: String,
        email: String,
        nipOrNuptk: String,
        requestedRole: UserRole
    ): AuthResult
    fun submitDocument(
        title: String,
        categoryId: String,
        documentNumber: String,
        actor: UserProfile,
        fileUrlOrPath: String? = null
    ): SchoolDocument
    fun reviewDocument(
        documentId: String,
        newStatus: VerificationStatus,
        notes: String,
        reviewer: UserProfile
    ): Boolean
    fun approveUser(targetUid: String, actor: UserProfile): Boolean
    fun changeUserRole(targetUid: String, newRole: UserRole, actor: UserProfile): Boolean
}

class DemoSiadsRepository : SiadsRepository {

    private val users = DemoPreviewData.demoUsers.toMutableList()
    private val teachers = DemoPreviewData.demoTeachers.toMutableList()
    private val categories = DemoPreviewData.demoCategories.toMutableList()
    private val standards = DemoPreviewData.demoStandards.toMutableList()
    private val documents = DemoPreviewData.demoDocuments.toMutableList()
    private val evidence = DemoPreviewData.demoEvidence.toMutableList()
    private val verifications = DemoPreviewData.demoVerifications.toMutableList()
    private val activityLogs = DemoPreviewData.demoActivityLogs.toMutableList()

    override fun getSnapshot(): SiadsSnapshot = SiadsSnapshot(
        mode = DataEnvironmentMode.DEMO_PREVIEW,
        users = users.toList(),
        teachers = teachers.toList(),
        categories = categories.toList(),
        standards = standards.toList(),
        documents = documents.toList(),
        evidence = evidence.toList(),
        verifications = verifications.toList(),
        activityLogs = activityLogs.toList()
    )

    override fun loginWithEmail(email: String, password: String): AuthResult {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || password.length < 4) {
            return AuthResult(
                success = false,
                message = "Masukkan email sekolah dan kata sandi minimal 4 karakter."
            )
        }
        val matched = users.firstOrNull { it.email.lowercase() == cleanEmail }
            ?: return AuthResult(
                success = false,
                message = "Akun tidak ditemukan pada data Demo Preview. Gunakan tombol Akun Demo atau Daftar Akun Baru."
            )
        if (!matched.isApprovedByAdmin) {
            return AuthResult(
                success = false,
                message = "Akun ${matched.fullName} masih menunggu persetujuan Admin sekolah."
            )
        }
        appendLog(
            actor = matched,
            action = "LOGIN_DEMO_EMAIL",
            targetCollection = FirestoreCollections.USERS,
            detail = "Masuk melalui form login (Mode Demo Preview)"
        )
        return AuthResult(
            success = true,
            user = matched,
            message = "Berhasil masuk sebagai ${matched.role.label}."
        )
    }

    override fun loginWithDemoAccount(role: UserRole): AuthResult {
        val matched = users.firstOrNull { it.role == role && it.isDemoAccount }
            ?: return AuthResult(
                success = false,
                message = "Akun demo untuk peran ${role.label} tidak tersedia."
            )
        appendLog(
            actor = matched,
            action = "LOGIN_DEMO_SWITCH",
            targetCollection = FirestoreCollections.USERS,
            detail = "Simulasi masuk cepat peran ${role.badgeCode} (Demo Preview)"
        )
        return AuthResult(
            success = true,
            user = matched,
            message = "Masuk ke mode preview sebagai ${matched.fullName} (${role.badgeCode})."
        )
    }

    /**
     * KEAMANAN RBAC:
     * Pendaftar mandiri TIDAK PERNAH bisa memperoleh peran ADMIN atau KEPALA_SEKOLAH.
     * Meskipun parameter requestedRole bernilai ADMIN, fungsi ini menolaknya/menurunkannya
     * menjadi GURU dan mewajibkan persetujuan ADMIN terlebih dahulu.
     */
    override fun registerSelfServiceUser(
        fullName: String,
        email: String,
        nipOrNuptk: String,
        requestedRole: UserRole
    ): AuthResult {
        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim().lowercase()
        if (trimmedName.length < 3 || !trimmedEmail.contains("@")) {
            return AuthResult(
                success = false,
                message = "Nama lengkap dan format email valid wajib diisi."
            )
        }
        if (users.any { it.email.lowercase() == trimmedEmail }) {
            return AuthResult(
                success = false,
                message = "Email sudah terdaftar pada daftar akun preview."
            )
        }

        // Proteksi keras: jangan pernah berikan ADMIN atau KEPALA_SEKOLAH ke pendaftar mandiri
        val safeAssignedRole = when {
            requestedRole == UserRole.ADMIN || requestedRole == UserRole.KEPALA_SEKOLAH -> UserRole.GURU
            requestedRole.allowSelfRegistration -> requestedRole
            else -> UserRole.GURU
        }

        val newUser = UserProfile(
            uid = "reg-${UUID.randomUUID().toString().take(8)}",
            fullName = trimmedName,
            email = trimmedEmail,
            role = safeAssignedRole,
            nipOrNuptk = nipOrNuptk.ifBlank { "-" },
            isActive = true,
            isApprovedByAdmin = false, // Wajib disetujui Admin sebelum akses penuh
            createdAtIso = "2026-11-22T10:30:00Z",
            isDemoAccount = true
        )
        users.add(0, newUser)

        appendLog(
            actor = newUser,
            action = "SELF_REGISTER_USER",
            targetCollection = FirestoreCollections.USERS,
            detail = "Pendaftaran mandiri (${safeAssignedRole.badgeCode}) menunggu persetujuan Admin"
        )

        return AuthResult(
            success = true,
            user = null, // Tidak auto-login sebagai admin; perlu approval admin
            message = "Pendaftaran berhasil sebagai ${safeAssignedRole.label} (non-admin). " +
                "Hak akses Administrator tidak diberikan pada pendaftaran mandiri. " +
                "Admin dapat menyetujui akun ini melalui menu Manajemen Pengguna."
        )
    }

    override fun submitDocument(
        title: String,
        categoryId: String,
        documentNumber: String,
        actor: UserProfile,
        fileUrlOrPath: String?
    ): SchoolDocument {
        val matchedStandard = standards.firstOrNull { it.categoryId == categoryId }
        val category = CategoryType.fromId(categoryId)
        val resolvedPath = fileUrlOrPath?.takeIf { it.isNotBlank() }
            ?: "demo://documents/${title.trim().lowercase().replace(' ', '_')}.pdf"
        val isDriveUrl = resolvedPath.startsWith("https://drive.google.com/")
        val newDoc = SchoolDocument(
            id = "doc-${UUID.randomUUID().toString().take(6)}",
            title = title.trim(),
            categoryId = category.id,
            standardId = matchedStandard?.id ?: "std-general",
            documentNumber = documentNumber.ifBlank { "421.2/NEW/SDN1-NGR/2026" },
            schoolYear = "2026/2027",
            uploadedByUid = actor.uid,
            uploadedByName = actor.fullName,
            fileUrlOrPath = resolvedPath,
            status = VerificationStatus.MENUNGGU,
            updatedAtIso = "2026-11-22"
        )
        documents.add(0, newDoc)
        verifications.add(
            0,
            VerificationRecord(
                id = "ver-${UUID.randomUUID().toString().take(6)}",
                targetId = newDoc.id,
                targetType = "DOCUMENT",
                targetTitle = newDoc.title,
                categoryId = category.id,
                reviewerUid = "-",
                reviewerName = "Menunggu Peninjauan",
                status = VerificationStatus.MENUNGGU,
                notes = if (isDriveUrl) {
                    "File tersimpan di Google Drive folder sekolah ($resolvedPath)."
                } else {
                    "Dokumen ditambahkan pada mode Demo Preview."
                },
                reviewedAtIso = "2026-11-22"
            )
        )
        appendLog(
            actor = actor,
            action = if (isDriveUrl) "UPLOAD_GOOGLE_DRIVE" else "SUBMIT_DOCUMENT",
            targetCollection = FirestoreCollections.DOCUMENTS,
            detail = if (isDriveUrl) {
                "Mengunggah '${newDoc.title}' ke Google Drive (${category.shortTitle})"
            } else {
                "Menambahkan dokumen '${newDoc.title}' pada kategori ${category.shortTitle}"
            }
        )
        return newDoc
    }

    override fun reviewDocument(
        documentId: String,
        newStatus: VerificationStatus,
        notes: String,
        reviewer: UserProfile
    ): Boolean {
        if (reviewer.role != UserRole.KEPALA_SEKOLAH && reviewer.role != UserRole.ADMIN) {
            return false
        }
        val docIndex = documents.indexOfFirst { it.id == documentId }
        if (docIndex == -1) return false

        val oldDoc = documents[docIndex]
        documents[docIndex] = oldDoc.copy(status = newStatus, updatedAtIso = "2026-11-22")

        val verIndex = verifications.indexOfFirst { it.targetId == documentId }
        val updatedVer = VerificationRecord(
            id = if (verIndex >= 0) verifications[verIndex].id else "ver-${UUID.randomUUID().toString().take(6)}",
            targetId = documentId,
            targetType = "DOCUMENT",
            targetTitle = oldDoc.title,
            categoryId = oldDoc.categoryId,
            reviewerUid = reviewer.uid,
            reviewerName = reviewer.fullName,
            status = newStatus,
            notes = notes.ifBlank { "Diverifikasi oleh ${reviewer.fullName}" },
            reviewedAtIso = "2026-11-22"
        )
        if (verIndex >= 0) {
            verifications[verIndex] = updatedVer
        } else {
            verifications.add(0, updatedVer)
        }

        appendLog(
            actor = reviewer,
            action = "VERIFY_DOCUMENT",
            targetCollection = FirestoreCollections.VERIFICATIONS,
            detail = "Status '${oldDoc.title}' diubah menjadi ${newStatus.label}"
        )
        return true
    }

    override fun approveUser(targetUid: String, actor: UserProfile): Boolean {
        if (actor.role != UserRole.ADMIN) return false
        val idx = users.indexOfFirst { it.uid == targetUid }
        if (idx == -1) return false
        val target = users[idx]
        users[idx] = target.copy(isApprovedByAdmin = true, isActive = true)
        appendLog(
            actor = actor,
            action = "APPROVE_USER",
            targetCollection = FirestoreCollections.USERS,
            detail = "Menyetujui akun ${target.fullName} (${target.role.badgeCode})"
        )
        return true
    }

    override fun changeUserRole(targetUid: String, newRole: UserRole, actor: UserProfile): Boolean {
        if (actor.role != UserRole.ADMIN) return false
        val idx = users.indexOfFirst { it.uid == targetUid }
        if (idx == -1) return false
        val target = users[idx]
        users[idx] = target.copy(role = newRole)
        appendLog(
            actor = actor,
            action = "CHANGE_ROLE",
            targetCollection = FirestoreCollections.USERS,
            detail = "Mengubah peran ${target.fullName} menjadi ${newRole.badgeCode}"
        )
        return true
    }

    private fun appendLog(
        actor: UserProfile,
        action: String,
        targetCollection: String,
        detail: String
    ) {
        activityLogs.add(
            0,
            ActivityLog(
                id = "log-${UUID.randomUUID().toString().take(6)}",
                actorUid = actor.uid,
                actorName = actor.fullName,
                actorRole = actor.role,
                action = action,
                targetCollection = targetCollection,
                detail = detail,
                timestampIso = "2026-11-22 10:45"
            )
        )
    }
}
