export type UserRole = "ADMIN" | "KEPALA_SEKOLAH" | "GURU" | "OPERATOR";

export type CategoryId =
  | "pengembangan_diri"
  | "lingkungan"
  | "pembelajaran"
  | "tata_kelola_administrasi"
  | "profil_sekolah";

export type VerificationStatus = "MENUNGGU" | "DISETUJUI" | "PERLU_REVISI";

export interface CategoryMeta {
  id: CategoryId;
  title: string;
  shortTitle: string;
  subtitle: string;
  standardCode: string;
  bgClass: string;
  textClass: string;
  accentHex: string;
}

export interface UserProfile {
  uid: string;
  fullName: string;
  email: string;
  role: UserRole;
  nipOrNuptk: string;
  isActive: boolean;
  isApprovedByAdmin: boolean;
  createdAtIso: string;
  isDemoAccount?: boolean;
}

export interface TeacherRecord {
  id: string;
  fullName: string;
  nip: string;
  position: string;
  classAssigned: string;
  certificationStatus: string;
  phone: string;
  isActive: boolean;
}

export interface CategoryItem {
  id: CategoryId;
  description: string;
  coordinatorName: string;
  targetDocumentCount: number;
  sortOrder: number;
}

export interface SchoolDocument {
  id: string;
  title: string;
  categoryId: CategoryId;
  standardId: string;
  documentNumber: string;
  schoolYear: string;
  uploadedByUid: string;
  uploadedByName: string;
  fileUrlOrPath: string;
  status: VerificationStatus;
  updatedAtIso: string;
}

export interface AccreditationStandard {
  id: string;
  code: string;
  title: string;
  categoryId: CategoryId;
  indicatorSummary: string;
  weightScore: number;
  maxScore: number;
}

export interface EvidenceItem {
  id: string;
  title: string;
  categoryId: CategoryId;
  documentId: string;
  mediaType: string;
  description: string;
  capturedDateIso: string;
  submittedByName: string;
  verificationStatus: VerificationStatus;
}

export interface VerificationRecord {
  id: string;
  targetId: string;
  targetType: "DOCUMENT" | "EVIDENCE";
  targetTitle: string;
  categoryId: CategoryId;
  reviewerUid: string;
  reviewerName: string;
  status: VerificationStatus;
  notes: string;
  reviewedAtIso: string;
}

export interface ActivityLog {
  id: string;
  actorUid: string;
  actorName: string;
  actorRole: UserRole;
  action: string;
  targetCollection: string;
  detail: string;
  timestampIso: string;
}

export const CATEGORIES_META: CategoryMeta[] = [
  {
    id: "pengembangan_diri",
    title: "PENGEMBANGAN DIRI",
    shortTitle: "Pengembangan Diri",
    subtitle: "Ekstrakurikuler, prestasi siswa, PKB guru & pelatihan kompetensi",
    standardCode: "STD-PD-01",
    bgClass: "bg-[#D32F2F] hover:bg-[#B71C1C]",
    textClass: "text-white",
    accentHex: "#D32F2F"
  },
  {
    id: "lingkungan",
    title: "LINGKUNGAN",
    shortTitle: "Lingkungan",
    subtitle: "Sekolah Adiwiyata, UKS, sanitasi, kebersihan & ruang terbuka hijau",
    standardCode: "STD-LG-02",
    bgClass: "bg-[#2E7D32] hover:bg-[#1B5E20]",
    textClass: "text-white",
    accentHex: "#2E7D32"
  },
  {
    id: "pembelajaran",
    title: "PEMBELAJARAN",
    shortTitle: "Pembelajaran",
    subtitle: "Kurikulum Merdeka, RPP/Modul Ajar, asesmen & supervisi kelas",
    standardCode: "STD-PB-03",
    bgClass: "bg-[#7B1FA2] hover:bg-[#4A148C]",
    textClass: "text-white",
    accentHex: "#7B1FA2"
  },
  {
    id: "tata_kelola_administrasi",
    title: "TATA KELOLA DAN ADMINISTRASI",
    shortTitle: "Tata Kelola & Adm.",
    subtitle: "RKAS/BOS, surat-menyurat, inventaris barang & administrasi kepegawaian",
    standardCode: "STD-TK-04",
    bgClass: "bg-[#84CC16] hover:bg-[#65A30D]",
    textClass: "text-[#142407]",
    accentHex: "#84CC16"
  },
  {
    id: "profil_sekolah",
    title: "PROFIL SEKOLAH",
    shortTitle: "Profil Sekolah",
    subtitle: "Identitas sekolah, NPSN, visi misi, struktur organisasi & data GTK",
    standardCode: "STD-PS-05",
    bgClass: "bg-[#C2185B] hover:bg-[#880E4F]",
    textClass: "text-white",
    accentHex: "#C2185B"
  }
];
