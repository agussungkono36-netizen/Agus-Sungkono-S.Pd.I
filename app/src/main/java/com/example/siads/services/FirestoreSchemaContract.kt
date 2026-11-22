package com.example.siads.services

import com.example.siads.config.FirestoreCollections

/**
 * Kontrak struktur Firestore & Security Rules Blueprint untuk tahap integrasi produksi.
 * Tidak menjalankan koneksi jaringan sebelum Firebase resmi dikonfigurasi.
 */
object FirestoreSchemaContract {

    data class CollectionBlueprint(
        val collectionName: String,
        val primaryKey: String,
        val requiredFields: List<String>,
        val accessPolicySummary: String
    )

    val blueprints: List<CollectionBlueprint> = listOf(
        CollectionBlueprint(
            collectionName = FirestoreCollections.USERS,
            primaryKey = "uid",
            requiredFields = listOf("uid", "fullName", "email", "role", "isActive", "isApprovedByAdmin"),
            accessPolicySummary = "Pendaftar mandiri dilarang keras menulis role='ADMIN'. Hanya ADMIN yang dapat mengubah role."
        ),
        CollectionBlueprint(
            collectionName = FirestoreCollections.TEACHERS,
            primaryKey = "id",
            requiredFields = listOf("id", "fullName", "nip", "position", "classAssigned"),
            accessPolicySummary = "Dibaca oleh semua pengguna internal terautentikasi; dikelola oleh ADMIN & OPERATOR."
        ),
        CollectionBlueprint(
            collectionName = FirestoreCollections.CATEGORIES,
            primaryKey = "id",
            requiredFields = listOf("id", "code", "title", "sortOrder"),
            accessPolicySummary = "Publik dapat membaca 5 kategori utama; perubahan hanya oleh ADMIN."
        ),
        CollectionBlueprint(
            collectionName = FirestoreCollections.DOCUMENTS,
            primaryKey = "id",
            requiredFields = listOf("id", "title", "categoryId", "standardId", "uploadedByUid", "status"),
            accessPolicySummary = "GURU & OPERATOR dapat mengunggah; KEPALA_SEKOLAH & ADMIN dapat memverifikasi."
        ),
        CollectionBlueprint(
            collectionName = FirestoreCollections.ACCREDITATION_STANDARDS,
            primaryKey = "id",
            requiredFields = listOf("id", "code", "title", "categoryId", "maxScore"),
            accessPolicySummary = "Dibaca publik/internal; dikelola ADMIN & KEPALA_SEKOLAH."
        ),
        CollectionBlueprint(
            collectionName = FirestoreCollections.EVIDENCE,
            primaryKey = "id",
            requiredFields = listOf("id", "title", "categoryId", "documentId", "verificationStatus"),
            accessPolicySummary = "Bukti fisik diunggah GURU/OPERATOR; status diverifikasi oleh KEPALA_SEKOLAH."
        ),
        CollectionBlueprint(
            collectionName = FirestoreCollections.VERIFICATIONS,
            primaryKey = "id",
            requiredFields = listOf("id", "targetId", "targetType", "reviewerUid", "status"),
            accessPolicySummary = "Hanya KEPALA_SEKOLAH dan ADMIN yang dapat membuat/memperbarui catatan verifikasi."
        ),
        CollectionBlueprint(
            collectionName = FirestoreCollections.ACTIVITY_LOGS,
            primaryKey = "id",
            requiredFields = listOf("id", "actorUid", "actorRole", "action", "timestampIso"),
            accessPolicySummary = "Append-only untuk aksi terautentikasi; tidak dapat dihapus dari sisi klien."
        )
    )
}
