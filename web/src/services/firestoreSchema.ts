import { FIRESTORE_COLLECTIONS } from "../config/firebase";

export interface FirestoreCollectionBlueprint {
  collectionName: string;
  purpose: string;
  requiredFields: string[];
  accessSummary: string;
}

/**
 * Kontrak struktur awal 8 koleksi Firestore SIADS SD Negeri 1 Ngantru.
 */
export const FIRESTORE_SCHEMA_BLUEPRINTS: FirestoreCollectionBlueprint[] = [
  {
    collectionName: FIRESTORE_COLLECTIONS.USERS,
    purpose: "Profil pengguna terautentikasi dan peran RBAC (ADMIN, KEPALA_SEKOLAH, GURU, OPERATOR).",
    requiredFields: [
      "uid",
      "fullName",
      "email",
      "role",
      "nipOrNuptk",
      "isActive",
      "isApprovedByAdmin",
      "createdAtIso"
    ],
    accessSummary:
      "Pengguna membaca profilnya sendiri; pendaftaran mandiri wajib non-ADMIN; perubahan role hanya ADMIN."
  },
  {
    collectionName: FIRESTORE_COLLECTIONS.TEACHERS,
    purpose: "Master data Pendidik & Tenaga Kependidikan (GTK) SD Negeri 1 Ngantru.",
    requiredFields: [
      "id",
      "fullName",
      "nip",
      "position",
      "classAssigned",
      "certificationStatus",
      "phone",
      "isActive"
    ],
    accessSummary: "Dibaca staf terverifikasi; dikelola oleh ADMIN & OPERATOR."
  },
  {
    collectionName: FIRESTORE_COLLECTIONS.CATEGORIES,
    purpose:
      "5 kategori utama SIADS: pengembangan_diri, lingkungan, pembelajaran, tata_kelola_administrasi, profil_sekolah.",
    requiredFields: ["id", "description", "coordinatorName", "targetDocumentCount", "sortOrder"],
    accessSummary: "Dibaca publik/portal; diubah oleh ADMIN."
  },
  {
    collectionName: FIRESTORE_COLLECTIONS.DOCUMENTS,
    purpose: "Arsip dokumen resmi akreditasi dan tautan berkas Google Drive folder sekolah.",
    requiredFields: [
      "id",
      "title",
      "categoryId",
      "standardId",
      "documentNumber",
      "schoolYear",
      "uploadedByUid",
      "uploadedByName",
      "fileUrlOrPath",
      "status",
      "updatedAtIso"
    ],
    accessSummary: "Dibuat GURU/OPERATOR/ADMIN; status diverifikasi KEPALA_SEKOLAH atau ADMIN."
  },
  {
    collectionName: FIRESTORE_COLLECTIONS.ACCREDITATION_STANDARDS,
    purpose: "Standar & indikator penilaian akreditasi per kategori.",
    requiredFields: ["id", "code", "title", "categoryId", "indicatorSummary", "weightScore", "maxScore"],
    accessSummary: "Dibaca staf sekolah; dikelola oleh ADMIN & KEPALA_SEKOLAH."
  },
  {
    collectionName: FIRESTORE_COLLECTIONS.EVIDENCE,
    purpose: "Bukti fisik, foto kegiatan, dan lampiran pendukung dokumen akreditasi.",
    requiredFields: [
      "id",
      "title",
      "categoryId",
      "documentId",
      "mediaType",
      "description",
      "capturedDateIso",
      "submittedByName",
      "verificationStatus"
    ],
    accessSummary: "Diunggah GURU/OPERATOR; diverifikasi KEPALA_SEKOLAH/ADMIN."
  },
  {
    collectionName: FIRESTORE_COLLECTIONS.VERIFICATIONS,
    purpose: "Riwayat pemeriksaan, catatan revisi, dan persetujuan dokumen oleh Kepala Sekolah/Admin.",
    requiredFields: [
      "id",
      "targetId",
      "targetType",
      "targetTitle",
      "categoryId",
      "reviewerUid",
      "reviewerName",
      "status",
      "notes",
      "reviewedAtIso"
    ],
    accessSummary: "Hanya dapat ditulis oleh KEPALA_SEKOLAH atau ADMIN."
  },
  {
    collectionName: FIRESTORE_COLLECTIONS.ACTIVITY_LOGS,
    purpose: "Audit trail aktivitas unggah berkas, verifikasi, dan perubahan peran pengguna.",
    requiredFields: [
      "id",
      "actorUid",
      "actorName",
      "actorRole",
      "action",
      "targetCollection",
      "detail",
      "timestampIso"
    ],
    accessSummary: "Append-only untuk aksi terautentikasi; dibaca oleh ADMIN & KEPALA_SEKOLAH."
  }
];
