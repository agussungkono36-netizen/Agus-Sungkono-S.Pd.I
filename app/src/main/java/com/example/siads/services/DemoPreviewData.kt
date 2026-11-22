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

/**
 * DATA DEMO TERISOLASI KHUSUS UNTUK PREVIEW TAHAP PERTAMA.
 * Dipisahkan sepenuhnya dari konfigurasi & koleksi produksi Firebase Firestore.
 * Seluruh entitas di file ini hanya dimuat saat DataEnvironmentMode == DEMO_PREVIEW.
 */
object DemoPreviewData {

    const val ENVIRONMENT_MODE_TAG: String = "DEMO_PREVIEW_ISOLATED"
    val mode: DataEnvironmentMode = DataEnvironmentMode.DEMO_PREVIEW

    val demoUsers: List<UserProfile> = listOf(
        UserProfile(
            uid = "demo-admin-01",
            fullName = "Budi Santoso, S.Kom.",
            email = "admin@sdn1ngantru.sch.id",
            role = UserRole.ADMIN,
            nipOrNuptk = "198704122015031002",
            isActive = true,
            isApprovedByAdmin = true,
            createdAtIso = "2026-07-01T08:00:00Z",
            isDemoAccount = true
        ),
        UserProfile(
            uid = "demo-kepsek-01",
            fullName = "Dra. Siti Aminah, M.Pd.",
            email = "kepsek@sdn1ngantru.sch.id",
            role = UserRole.KEPALA_SEKOLAH,
            nipOrNuptk = "196908151994032004",
            isActive = true,
            isApprovedByAdmin = true,
            createdAtIso = "2026-07-01T08:05:00Z",
            isDemoAccount = true
        ),
        UserProfile(
            uid = "demo-guru-01",
            fullName = "Ahmad Fauzi, S.Pd.SD.",
            email = "guru@sdn1ngantru.sch.id",
            role = UserRole.GURU,
            nipOrNuptk = "199105202019031007",
            isActive = true,
            isApprovedByAdmin = true,
            createdAtIso = "2026-07-02T09:00:00Z",
            isDemoAccount = true
        ),
        UserProfile(
            uid = "demo-operator-01",
            fullName = "Rina Wahyuni, A.Md.",
            email = "operator@sdn1ngantru.sch.id",
            role = UserRole.OPERATOR,
            nipOrNuptk = "2456771672130042",
            isActive = true,
            isApprovedByAdmin = true,
            createdAtIso = "2026-07-02T09:15:00Z",
            isDemoAccount = true
        )
    )

    val demoTeachers: List<TeacherRecord> = listOf(
        TeacherRecord(
            id = "tch-01",
            fullName = "Dra. Siti Aminah, M.Pd.",
            nip = "196908151994032004",
            position = "Kepala Sekolah",
            classAssigned = "Pimpinan Sekolah",
            certificationStatus = "Tersertifikasi",
            phone = "0812-3456-7801",
            isActive = true
        ),
        TeacherRecord(
            id = "tch-02",
            fullName = "Ahmad Fauzi, S.Pd.SD.",
            nip = "199105202019031007",
            position = "Guru Kelas VI",
            classAssigned = "Kelas VI-A",
            certificationStatus = "Tersertifikasi",
            phone = "0812-3456-7802",
            isActive = true
        ),
        TeacherRecord(
            id = "tch-03",
            fullName = "Dewi Lestari, S.Pd.",
            nip = "199311082020122011",
            position = "Guru Kelas III",
            classAssigned = "Kelas III-A",
            certificationStatus = "PPG Dalam Jabatan",
            phone = "0812-3456-7803",
            isActive = true
        ),
        TeacherRecord(
            id = "tch-04",
            fullName = "Hendra Pratama, S.Pd.",
            nip = "198902142019031005",
            position = "Guru PJOK",
            classAssigned = "Kelas I - VI",
            certificationStatus = "Tersertifikasi",
            phone = "0812-3456-7804",
            isActive = true
        )
    )

    val demoCategories: List<CategoryItem> = listOf(
        CategoryItem(
            id = CategoryType.PENGEMBANGAN_DIRI.id,
            type = CategoryType.PENGEMBANGAN_DIRI,
            description = "Program pembinaan bakat siswa, Pramuka, literasi pagi, serta pengembangan keprofesian berkelanjutan (PKB) guru.",
            coordinatorName = "Ahmad Fauzi, S.Pd.SD.",
            targetDocumentCount = 12,
            sortOrder = 1
        ),
        CategoryItem(
            id = CategoryType.LINGKUNGAN.id,
            type = CategoryType.LINGKUNGAN,
            description = "Pengelolaan lingkungan sekolah sehat, taman edukasi, bank sampah mini, UKS, dan fasilitas sanitasi layak anak.",
            coordinatorName = "Dewi Lestari, S.Pd.",
            targetDocumentCount = 10,
            sortOrder = 2
        ),
        CategoryItem(
            id = CategoryType.PEMBELAJARAN.id,
            type = CategoryType.PEMBELAJARAN,
            description = "Perangkat ajar Kurikulum Merdeka, ATP, Modul Ajar, asesmen diagnostik, dan laporan supervisi akademik.",
            coordinatorName = "Dra. Siti Aminah, M.Pd.",
            targetDocumentCount = 18,
            sortOrder = 3
        ),
        CategoryItem(
            id = CategoryType.TATA_KELOLA_ADMINISTRASI.id,
            type = CategoryType.TATA_KELOLA_ADMINISTRASI,
            description = "RKAS dan pelaporan dana BOS, buku induk siswa, arsip persuratan dinas, serta inventaris sarana prasarana.",
            coordinatorName = "Rina Wahyuni, A.Md.",
            targetDocumentCount = 15,
            sortOrder = 4
        ),
        CategoryItem(
            id = CategoryType.PROFIL_SEKOLAH.id,
            type = CategoryType.PROFIL_SEKOLAH,
            description = "Identitas resmi SD Negeri 1 Ngantru, SK pendirian, visi misi tujuan sekolah, peta lokasi, dan rekap Dapodik.",
            coordinatorName = "Rina Wahyuni, A.Md.",
            targetDocumentCount = 8,
            sortOrder = 5
        )
    )

    val demoStandards: List<AccreditationStandard> = listOf(
        AccreditationStandard(
            id = "std-pd-1",
            code = "IASP-PD.01",
            title = "Pembinaan Prestasi & Ekstrakurikuler",
            categoryId = CategoryType.PENGEMBANGAN_DIRI.id,
            indicatorSummary = "Sekolah memfasilitasi kegiatan ekstrakurikuler wajib & pilihan dengan dokumentasi kehadiran dan prestasi.",
            weightScore = 88,
            maxScore = 100
        ),
        AccreditationStandard(
            id = "std-pd-2",
            code = "IASP-PD.02",
            title = "Pengembangan Kompetensi Pendidik",
            categoryId = CategoryType.PENGEMBANGAN_DIRI.id,
            indicatorSummary = "Guru mengikuti KKG, pelatihan PMM/komunitas belajar, dan desiminasi praktik baik.",
            weightScore = 92,
            maxScore = 100
        ),
        AccreditationStandard(
            id = "std-lg-1",
            code = "IASP-LG.01",
            title = "Kebersihan, Sanitasi & Lingkungan Hijau",
            categoryId = CategoryType.LINGKUNGAN.id,
            indicatorSummary = "Ketersediaan toilet bersih terpisah, tempat cuci tangan, pengelolaan sampah terpilah, dan taman sekolah.",
            weightScore = 90,
            maxScore = 100
        ),
        AccreditationStandard(
            id = "std-pb-1",
            code = "IASP-PB.01",
            title = "Perencanaan & Pelaksanaan Pembelajaran Aktif",
            categoryId = CategoryType.PEMBELAJARAN.id,
            indicatorSummary = "Seluruh guru menyusun Modul Ajar berdiferensiasi dan melakukan refleksi pembelajaran.",
            weightScore = 94,
            maxScore = 100
        ),
        AccreditationStandard(
            id = "std-tk-1",
            code = "IASP-TK.01",
            title = "Tata Kelola Keuangan & Administrasi Sekolah",
            categoryId = CategoryType.TATA_KELOLA_ADMINISTRASI.id,
            indicatorSummary = "Pengelolaan RKAS transparan, arsip surat terindeks digital, dan inventaris barang mutakhir.",
            weightScore = 89,
            maxScore = 100
        ),
        AccreditationStandard(
            id = "std-ps-1",
            code = "IASP-PS.01",
            title = "Validitas Profil & Visi Misi Sekolah",
            categoryId = CategoryType.PROFIL_SEKOLAH.id,
            indicatorSummary = "Visi, misi, dan data pokok sekolah terdokumentasi sah serta disosialisasikan kepada warga sekolah.",
            weightScore = 96,
            maxScore = 100
        )
    )

    val demoDocuments: List<SchoolDocument> = listOf(
        SchoolDocument(
            id = "doc-01",
            title = "SK Pembina Ekstrakurikuler Pramuka & Tari",
            categoryId = CategoryType.PENGEMBANGAN_DIRI.id,
            standardId = "std-pd-1",
            documentNumber = "421.2/018/SDN1-NGR/2026",
            schoolYear = "2026/2027",
            uploadedByUid = "demo-guru-01",
            uploadedByName = "Ahmad Fauzi, S.Pd.SD.",
            fileUrlOrPath = "demo://documents/sk_ekskul_2026.pdf",
            status = VerificationStatus.DISETUJUI,
            updatedAtIso = "2026-11-10"
        ),
        SchoolDocument(
            id = "doc-02",
            title = "Sertifikat Komunitas Belajar & Workshop IKM",
            categoryId = CategoryType.PENGEMBANGAN_DIRI.id,
            standardId = "std-pd-2",
            documentNumber = "421.2/024/KKG-NGR/2026",
            schoolYear = "2026/2027",
            uploadedByUid = "demo-guru-01",
            uploadedByName = "Ahmad Fauzi, S.Pd.SD.",
            fileUrlOrPath = "demo://documents/sertifikat_kkg_2026.pdf",
            status = VerificationStatus.MENUNGGU,
            updatedAtIso = "2026-11-18"
        ),
        SchoolDocument(
            id = "doc-03",
            title = "Program Kerja Sekolah Sehat & Adiwiyata",
            categoryId = CategoryType.LINGKUNGAN.id,
            standardId = "std-lg-1",
            documentNumber = "421.2/031/UKS-NGR/2026",
            schoolYear = "2026/2027",
            uploadedByUid = "demo-operator-01",
            uploadedByName = "Rina Wahyuni, A.Md.",
            fileUrlOrPath = "demo://documents/program_adiwiyata_2026.pdf",
            status = VerificationStatus.DISETUJUI,
            updatedAtIso = "2026-11-12"
        ),
        SchoolDocument(
            id = "doc-04",
            title = "Modul Ajar Fase C Kelas VI Semester Ganjil",
            categoryId = CategoryType.PEMBELAJARAN.id,
            standardId = "std-pb-1",
            documentNumber = "421.2/045/KUR-NGR/2026",
            schoolYear = "2026/2027",
            uploadedByUid = "demo-guru-01",
            uploadedByName = "Ahmad Fauzi, S.Pd.SD.",
            fileUrlOrPath = "demo://documents/modul_ajar_fase_c.pdf",
            status = VerificationStatus.MENUNGGU,
            updatedAtIso = "2026-11-20"
        ),
        SchoolDocument(
            id = "doc-05",
            title = "Laporan Realisasi RKAS & BOS Triwulan III",
            categoryId = CategoryType.TATA_KELOLA_ADMINISTRASI.id,
            standardId = "std-tk-1",
            documentNumber = "900/019/BOS-NGR/2026",
            schoolYear = "2026/2027",
            uploadedByUid = "demo-operator-01",
            uploadedByName = "Rina Wahyuni, A.Md.",
            fileUrlOrPath = "demo://documents/rkas_triwulan_3.pdf",
            status = VerificationStatus.DISETUJUI,
            updatedAtIso = "2026-11-14"
        ),
        SchoolDocument(
            id = "doc-06",
            title = "Dokumen Profil & Visi Misi SD Negeri 1 Ngantru",
            categoryId = CategoryType.PROFIL_SEKOLAH.id,
            standardId = "std-ps-1",
            documentNumber = "421.2/001/PROFIL-NGR/2026",
            schoolYear = "2026/2027",
            uploadedByUid = "demo-operator-01",
            uploadedByName = "Rina Wahyuni, A.Md.",
            fileUrlOrPath = "demo://documents/profil_sdn1_ngantru.pdf",
            status = VerificationStatus.DISETUJUI,
            updatedAtIso = "2026-11-05"
        )
    )

    val demoEvidence: List<EvidenceItem> = listOf(
        EvidenceItem(
            id = "evd-01",
            title = "Dokumentasi Latihan Pramuka Siaga & Penggalang",
            categoryId = CategoryType.PENGEMBANGAN_DIRI.id,
            documentId = "doc-01",
            mediaType = "Foto Kegiatan",
            description = "Kegiatan rutin hari Jumat di halaman utama sekolah bersama pembina.",
            capturedDateIso = "2026-11-08",
            submittedByName = "Ahmad Fauzi, S.Pd.SD.",
            verificationStatus = VerificationStatus.DISETUJUI
        ),
        EvidenceItem(
            id = "evd-02",
            title = "Foto Taman Toga & Pemilahan Sampah Organik",
            categoryId = CategoryType.LINGKUNGAN.id,
            documentId = "doc-03",
            mediaType = "Foto Sarpras",
            description = "Siswa melaksanakan piket kebun toga dan bank sampah kelas.",
            capturedDateIso = "2026-11-11",
            submittedByName = "Dewi Lestari, S.Pd.",
            verificationStatus = VerificationStatus.DISETUJUI
        ),
        EvidenceItem(
            id = "evd-03",
            title = "Praktik Pembelajaran IPAS Berbasis Proyek",
            categoryId = CategoryType.PEMBELAJARAN.id,
            documentId = "doc-04",
            mediaType = "Foto & Rubrik",
            description = "Kelompok siswa Kelas VI mempresentasikan proyek energi terbarukan.",
            capturedDateIso = "2026-11-19",
            submittedByName = "Ahmad Fauzi, S.Pd.SD.",
            verificationStatus = VerificationStatus.MENUNGGU
        ),
        EvidenceItem(
            id = "evd-04",
            title = "Papan Informasi Transparansi RKAS Sekolah",
            categoryId = CategoryType.TATA_KELOLA_ADMINISTRASI.id,
            documentId = "doc-05",
            mediaType = "Foto Dokumen",
            description = "Publikasi penggunaan dana BOS yang dapat diakses komite dan wali murid.",
            capturedDateIso = "2026-11-15",
            submittedByName = "Rina Wahyuni, A.Md.",
            verificationStatus = VerificationStatus.DISETUJUI
        )
    )

    val demoVerifications: List<VerificationRecord> = listOf(
        VerificationRecord(
            id = "ver-01",
            targetId = "doc-01",
            targetType = "DOCUMENT",
            targetTitle = "SK Pembina Ekstrakurikuler Pramuka & Tari",
            categoryId = CategoryType.PENGEMBANGAN_DIRI.id,
            reviewerUid = "demo-kepsek-01",
            reviewerName = "Dra. Siti Aminah, M.Pd.",
            status = VerificationStatus.DISETUJUI,
            notes = "Dokumen lengkap bertanda tangan kepala sekolah dan sesuai jadwal latihan.",
            reviewedAtIso = "2026-11-11"
        ),
        VerificationRecord(
            id = "ver-02",
            targetId = "doc-02",
            targetType = "DOCUMENT",
            targetTitle = "Sertifikat Komunitas Belajar & Workshop IKM",
            categoryId = CategoryType.PENGEMBANGAN_DIRI.id,
            reviewerUid = "demo-kepsek-01",
            reviewerName = "Dra. Siti Aminah, M.Pd.",
            status = VerificationStatus.MENUNGGU,
            notes = "Menunggu peninjauan lampiran daftar hadir desiminasi.",
            reviewedAtIso = "2026-11-19"
        ),
        VerificationRecord(
            id = "ver-03",
            targetId = "doc-04",
            targetType = "DOCUMENT",
            targetTitle = "Modul Ajar Fase C Kelas VI Semester Ganjil",
            categoryId = CategoryType.PEMBELAJARAN.id,
            reviewerUid = "demo-kepsek-01",
            reviewerName = "Dra. Siti Aminah, M.Pd.",
            status = VerificationStatus.MENUNGGU,
            notes = "Dalam antrean supervisi Kepala Sekolah.",
            reviewedAtIso = "2026-11-20"
        )
    )

    val demoActivityLogs: List<ActivityLog> = listOf(
        ActivityLog(
            id = "log-01",
            actorUid = "demo-guru-01",
            actorName = "Ahmad Fauzi, S.Pd.SD.",
            actorRole = UserRole.GURU,
            action = "UPLOAD_DOCUMENT",
            targetCollection = FirestoreCollections.DOCUMENTS,
            detail = "Mengunggah Modul Ajar Fase C Kelas VI Semester Ganjil (Mode Demo)",
            timestampIso = "2026-11-20 09:40"
        ),
        ActivityLog(
            id = "log-02",
            actorUid = "demo-kepsek-01",
            actorName = "Dra. Siti Aminah, M.Pd.",
            actorRole = UserRole.KEPALA_SEKOLAH,
            action = "VERIFY_DOCUMENT",
            targetCollection = FirestoreCollections.VERIFICATIONS,
            detail = "Menyetujui Laporan Realisasi RKAS & BOS Triwulan III",
            timestampIso = "2026-11-15 13:15"
        ),
        ActivityLog(
            id = "log-03",
            actorUid = "demo-operator-01",
            actorName = "Rina Wahyuni, A.Md.",
            actorRole = UserRole.OPERATOR,
            action = "UPDATE_PROFILE",
            targetCollection = FirestoreCollections.DOCUMENTS,
            detail = "Memperbarui dokumen Profil & Visi Misi SD Negeri 1 Ngantru",
            timestampIso = "2026-11-12 10:20"
        ),
        ActivityLog(
            id = "log-04",
            actorUid = "demo-admin-01",
            actorName = "Budi Santoso, S.Kom.",
            actorRole = UserRole.ADMIN,
            action = "AUDIT_SCHEMA",
            targetCollection = FirestoreCollections.USERS,
            detail = "Menyiapkan struktur 8 koleksi Firestore & kebijakan akses peran",
            timestampIso = "2026-11-10 08:00"
        )
    )
}
