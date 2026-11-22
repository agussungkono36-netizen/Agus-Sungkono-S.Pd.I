import type {
  AccreditationStandard,
  ActivityLog,
  CategoryItem,
  EvidenceItem,
  SchoolDocument,
  TeacherRecord,
  UserProfile,
  VerificationRecord
} from "../types/siads";

/**
 * DATA DEMO TERISOLASI UNTUK PREVIEW TAHAP PERTAMA
 * Dipisahkan sepenuhnya dari database Firestore produksi.
 */
export interface SiadsDemoSnapshot {
  users: UserProfile[];
  teachers: TeacherRecord[];
  categories: CategoryItem[];
  accreditationStandards: AccreditationStandard[];
  documents: SchoolDocument[];
  evidence: EvidenceItem[];
  verifications: VerificationRecord[];
  activityLogs: ActivityLog[];
}

export function createInitialDemoSnapshot(): SiadsDemoSnapshot {
  return {
    users: [
      {
        uid: "demo-admin-01",
        fullName: "Agus Sungkono, S.Pd",
        email: "admin.demo@sdnegeri1ngantru.schoolpro.id",
        role: "ADMIN",
        nipOrNuptk: "198204122009031004",
        isActive: true,
        isApprovedByAdmin: true,
        createdAtIso: "2026-01-10T08:00:00Z",
        isDemoAccount: true
      },
      {
        uid: "demo-kepsek-01",
        fullName: "Dra. Siti Aminah, M.Pd",
        email: "kepsek.demo@sdnegeri1ngantru.schoolpro.id",
        role: "KEPALA_SEKOLAH",
        nipOrNuptk: "197108151996032002",
        isActive: true,
        isApprovedByAdmin: true,
        createdAtIso: "2026-01-10T08:05:00Z",
        isDemoAccount: true
      },
      {
        uid: "demo-guru-01",
        fullName: "Budi Santoso, S.Pd.SD",
        email: "guru.demo@sdnegeri1ngantru.schoolpro.id",
        role: "GURU",
        nipOrNuptk: "198911232019031006",
        isActive: true,
        isApprovedByAdmin: true,
        createdAtIso: "2026-01-11T07:30:00Z",
        isDemoAccount: true
      },
      {
        uid: "demo-operator-01",
        fullName: "Rina Wahyuni, S.Kom",
        email: "operator.demo@sdnegeri1ngantru.schoolpro.id",
        role: "OPERATOR",
        nipOrNuptk: "3452768669130012",
        isActive: true,
        isApprovedByAdmin: true,
        createdAtIso: "2026-01-11T07:45:00Z",
        isDemoAccount: true
      }
    ],
    teachers: [
      {
        id: "t-01",
        fullName: "Dra. Siti Aminah, M.Pd",
        nip: "197108151996032002",
        position: "Kepala Sekolah",
        classAssigned: "Manajemen Sekolah",
        certificationStatus: "Tersertifikasi",
        phone: "0812-3400-1101",
        isActive: true
      },
      {
        id: "t-02",
        fullName: "Budi Santoso, S.Pd.SD",
        nip: "198911232019031006",
        position: "Guru Kelas VI",
        classAssigned: "Kelas 6A",
        certificationStatus: "Tersertifikasi",
        phone: "0813-5521-4402",
        isActive: true
      },
      {
        id: "t-03",
        fullName: "Dewi Lestari, S.Pd",
        nip: "199203192020122009",
        position: "Guru Kelas I & Koordinator Adiwiyata",
        classAssigned: "Kelas 1A",
        certificationStatus: "PPG Dalam Jabatan",
        phone: "0857-3391-8820",
        isActive: true
      },
      {
        id: "t-04",
        fullName: "Hendra Wijaya, S.Pd",
        nip: "199007042022211005",
        position: "Guru PJOK & Ekstrakurikuler",
        classAssigned: "Kelas 1 - 6",
        certificationStatus: "Tersertifikasi",
        phone: "0852-9012-7741",
        isActive: true
      }
    ],
    categories: [
      {
        id: "pengembangan_diri",
        description:
          "Dokumentasi pembinaan bakat, minat, ekstrakurikuler Pramuka, literasi, olahraga, seni, serta pengembangan keprofesian berkelanjutan guru.",
        coordinatorName: "Hendra Wijaya, S.Pd",
        targetDocumentCount: 12,
        sortOrder: 1
      },
      {
        id: "lingkungan",
        description:
          "Dokumen dan bukti fisik program Sekolah Adiwiyata, sanitasi, taman edukatif, pengelolaan sampah terpilah, dan Usaha Kesehatan Sekolah (UKS).",
        coordinatorName: "Dewi Lestari, S.Pd",
        targetDocumentCount: 10,
        sortOrder: 2
      },
      {
        id: "pembelajaran",
        description:
          "Perangkat Kurikulum Satuan Pendidikan (KSP), modul ajar berdiferensiasi, asesmen diagnostik/sumatif, proyek P5, dan supervisi akademik.",
        coordinatorName: "Budi Santoso, S.Pd.SD",
        targetDocumentCount: 18,
        sortOrder: 3
      },
      {
        id: "tata_kelola_administrasi",
        description:
          "Perencanaan RKAS/BOS, tata naskah dinas, inventarisasi aset KIB, buku induk peserta didik, dan laporan kinerja tahunan.",
        coordinatorName: "Rina Wahyuni, S.Kom",
        targetDocumentCount: 15,
        sortOrder: 4
      },
      {
        id: "profil_sekolah",
        description:
          "Identitas resmi SD Negeri 1 Ngantru, SK pendirian, NPSN, visi-misi BERLIAN DJITU, struktur organisasi, dan profil GTK.",
        coordinatorName: "Dra. Siti Aminah, M.Pd",
        targetDocumentCount: 8,
        sortOrder: 5
      }
    ],
    accreditationStandards: [
      {
        id: "std-pd-01",
        code: "STD-PD-01",
        title: "Pembinaan Minat, Bakat & Ekstrakurikuler Siswa",
        categoryId: "pengembangan_diri",
        indicatorSummary: "Program kerja, daftar hadir, dan rekap prestasi siswa tingkat kecamatan/kabupaten.",
        weightScore: 18,
        maxScore: 20
      },
      {
        id: "std-lg-02",
        code: "STD-LG-02",
        title: "Lingkungan Belajar Sehat, Aman & Ramah Anak",
        categoryId: "lingkungan",
        indicatorSummary: "Ketersediaan sarana sanitasi, ruang UKS aktif, penghijauan, dan piket kebersihan terstruktur.",
        weightScore: 19,
        maxScore: 20
      },
      {
        id: "std-pb-03",
        code: "STD-PB-03",
        title: "Perencanaan & Pelaksanaan Pembelajaran Interaktif",
        categoryId: "pembelajaran",
        indicatorSummary: "Kelengkapan ATP, modul ajar Kurikulum Merdeka, asesmen formatif, dan refleksi supervisi.",
        weightScore: 24,
        maxScore: 25
      },
      {
        id: "std-tk-04",
        code: "STD-TK-04",
        title: "Tata Kelola Administrasi, Keuangan & Aset",
        categoryId: "tata_kelola_administrasi",
        indicatorSummary: "Transparansi RKAS/BOS, arsip surat masuk-keluar, serta pemutakhiran Dapodik tepat waktu.",
        weightScore: 18,
        maxScore: 20
      },
      {
        id: "std-ps-05",
        code: "STD-PS-05",
        title: "Identitas Sekolah, Visi Misi & Kemitraan Komite",
        categoryId: "profil_sekolah",
        indicatorSummary: "Penetapan Visi SD BERLIAN DJITU, SK komite sekolah, dan dokumen akreditasi institusi.",
        weightScore: 14,
        maxScore: 15
      }
    ],
    documents: [
      {
        id: "doc-01",
        title: "Program Kerja Ekstrakurikuler Pramuka & Bola Voli",
        categoryId: "pengembangan_diri",
        standardId: "std-pd-01",
        documentNumber: "421.2/018/SDN1N/2026",
        schoolYear: "2025/2026",
        uploadedByUid: "demo-guru-01",
        uploadedByName: "Budi Santoso, S.Pd.SD",
        fileUrlOrPath: "https://drive.google.com/drive/folders/1rPq3eEgEZbO_uvFSEO7qEwWm4XFWoRVl",
        status: "DISETUJUI",
        updatedAtIso: "2026-02-14T09:15:00Z"
      },
      {
        id: "doc-02",
        title: "SK Tim Pelaksana Adiwiyata & Kebersihan Lingkungan",
        categoryId: "lingkungan",
        standardId: "std-lg-02",
        documentNumber: "421.2/024/SDN1N/2026",
        schoolYear: "2025/2026",
        uploadedByUid: "demo-operator-01",
        uploadedByName: "Rina Wahyuni, S.Kom",
        fileUrlOrPath: "https://drive.google.com/drive/folders/1rPq3eEgEZbO_uvFSEO7qEwWm4XFWoRVl",
        status: "MENUNGGU",
        updatedAtIso: "2026-02-18T11:20:00Z"
      },
      {
        id: "doc-03",
        title: "Dokumen Kurikulum Satuan Pendidikan (KSP) Tahun 2025/2026",
        categoryId: "pembelajaran",
        standardId: "std-pb-03",
        documentNumber: "421.2/009/SDN1N/2026",
        schoolYear: "2025/2026",
        uploadedByUid: "demo-guru-01",
        uploadedByName: "Budi Santoso, S.Pd.SD",
        fileUrlOrPath: "https://drive.google.com/drive/folders/1rPq3eEgEZbO_uvFSEO7qEwWm4XFWoRVl",
        status: "DISETUJUI",
        updatedAtIso: "2026-02-10T14:00:00Z"
      },
      {
        id: "doc-04",
        title: "Laporan Realisasi RKAS Dana BOS Triwulan I",
        categoryId: "tata_kelola_administrasi",
        standardId: "std-tk-04",
        documentNumber: "900/031/SDN1N/2026",
        schoolYear: "2025/2026",
        uploadedByUid: "demo-operator-01",
        uploadedByName: "Rina Wahyuni, S.Kom",
        fileUrlOrPath: "https://drive.google.com/drive/folders/1rPq3eEgEZbO_uvFSEO7qEwWm4XFWoRVl",
        status: "PERLU_REVISI",
        updatedAtIso: "2026-02-19T08:40:00Z"
      },
      {
        id: "doc-05",
        title: "Profil Resmi & Piagam Visi Misi SD Negeri 1 Ngantru",
        categoryId: "profil_sekolah",
        standardId: "std-ps-05",
        documentNumber: "421.2/001/SDN1N/2026",
        schoolYear: "2025/2026",
        uploadedByUid: "demo-admin-01",
        uploadedByName: "Agus Sungkono, S.Pd",
        fileUrlOrPath: "https://drive.google.com/drive/folders/1rPq3eEgEZbO_uvFSEO7qEwWm4XFWoRVl",
        status: "DISETUJUI",
        updatedAtIso: "2026-01-20T10:00:00Z"
      }
    ],
    evidence: [
      {
        id: "ev-01",
        title: "Dokumentasi Latihan Bola Voli Lapangan Berkilau SDN 1 Ngantru",
        categoryId: "pengembangan_diri",
        documentId: "doc-01",
        mediaType: "Foto Kegiatan",
        description: "Kegiatan pembinaan olahraga siswa kelas IV–VI di lapangan olahraga sekolah.",
        capturedDateIso: "2026-02-12",
        submittedByName: "Budi Santoso, S.Pd.SD",
        verificationStatus: "DISETUJUI"
      },
      {
        id: "ev-02",
        title: "Penataan Taman Hijau & Bank Sampah Halaman Sekolah",
        categoryId: "lingkungan",
        documentId: "doc-02",
        mediaType: "Foto Bukti Fisik",
        description: "Area taman pot bunga dan pemilahan sampah organik/anorganik di depan ruang kelas.",
        capturedDateIso: "2026-02-16",
        submittedByName: "Rina Wahyuni, S.Kom",
        verificationStatus: "MENUNGGU"
      },
      {
        id: "ev-03",
        title: "Foto Praktik Projek Penguatan Profil Pelajar Pancasila (P5)",
        categoryId: "pembelajaran",
        documentId: "doc-03",
        mediaType: "Foto & Lembar Kerja",
        description: "Karya kolaboratif siswa tema Kearifan Lokal Tulungagung.",
        capturedDateIso: "2026-02-11",
        submittedByName: "Budi Santoso, S.Pd.SD",
        verificationStatus: "DISETUJUI"
      }
    ],
    verifications: [
      {
        id: "ver-01",
        targetId: "doc-01",
        targetType: "DOCUMENT",
        targetTitle: "Program Kerja Ekstrakurikuler Pramuka & Bola Voli",
        categoryId: "pengembangan_diri",
        reviewerUid: "demo-kepsek-01",
        reviewerName: "Dra. Siti Aminah, M.Pd",
        status: "DISETUJUI",
        notes: "Dokumen lengkap sesuai kalender pendidikan dan jadwal pembina.",
        reviewedAtIso: "2026-02-15T10:15:00Z"
      },
      {
        id: "ver-02",
        targetId: "doc-04",
        targetType: "DOCUMENT",
        targetTitle: "Laporan Realisasi RKAS Dana BOS Triwulan I",
        categoryId: "tata_kelola_administrasi",
        reviewerUid: "demo-kepsek-01",
        reviewerName: "Dra. Siti Aminah, M.Pd",
        status: "PERLU_REVISI",
        notes: "Mohon lampirkan bukti kuitansi belanja ATK bulan Februari sebelum finalisasi.",
        reviewedAtIso: "2026-02-19T13:10:00Z"
      }
    ],
    activityLogs: [
      {
        id: "log-01",
        actorUid: "demo-kepsek-01",
        actorName: "Dra. Siti Aminah, M.Pd",
        actorRole: "KEPALA_SEKOLAH",
        action: "VERIFIKASI_DOKUMEN",
        targetCollection: "verifications",
        detail: "Menandai Laporan Realisasi RKAS Triwulan I sebagai PERLU_REVISI.",
        timestampIso: "2026-02-19T13:10:00Z"
      },
      {
        id: "log-02",
        actorUid: "demo-operator-01",
        actorName: "Rina Wahyuni, S.Kom",
        actorRole: "OPERATOR",
        action: "UNGGAH_DOKUMEN",
        targetCollection: "documents",
        detail: "Mengajukan SK Tim Pelaksana Adiwiyata ke antrean verifikasi.",
        timestampIso: "2026-02-18T11:20:00Z"
      },
      {
        id: "log-03",
        actorUid: "demo-admin-01",
        actorName: "Agus Sungkono, S.Pd",
        actorRole: "ADMIN",
        action: "INISIALISASI_PREVIEW",
        targetCollection: "categories",
        detail: "Menyiapkan 5 kategori akreditasi SIADS dalam mode Preview Demo terisolasi.",
        timestampIso: "2026-01-10T08:00:00Z"
      }
    ]
  };
}
