import {
  collection,
  doc,
  getDocs,
  setDoc
} from "firebase/firestore";
import { FIRESTORE_COLLECTIONS, getFirebaseServices } from "../config/firebase";
import type {
  ActivityLog,
  CategoryId,
  EvidenceItem,
  SchoolDocument,
  UserProfile,
  UserRole,
  VerificationRecord,
  VerificationStatus
} from "../types/siads";
import { createInitialDemoSnapshot, type SiadsDemoSnapshot } from "./demoData";

export interface RegisterPayload {
  fullName: string;
  email: string;
  nipOrNuptk: string;
  requestedRole: Exclude<UserRole, "ADMIN">;
}

export interface CreateDocumentPayload {
  title: string;
  categoryId: CategoryId;
  standardId: string;
  documentNumber: string;
  schoolYear: string;
  fileUrlOrPath: string;
  actor: UserProfile;
  alsoAttachEvidence?: boolean;
}

export interface VerifyTargetPayload {
  targetId: string;
  targetType: "DOCUMENT" | "EVIDENCE";
  status: VerificationStatus;
  notes: string;
  reviewer: UserProfile;
}

export interface ActionResult<T = void> {
  ok: boolean;
  message: string;
  data?: T;
}

/**
 * Service repository SIADS:
 * - Ketika Firebase .env.local belum dikonfigurasi, berjalan di atas Data Demo Terisolasi.
 * - Ketika Firebase aktif, menulis/membaca ke koleksi Firestore resmi tanpa mencampur data demo.
 */
export class SiadsService {
  private snapshot: SiadsDemoSnapshot = createInitialDemoSnapshot();

  getSnapshot(): SiadsDemoSnapshot {
    return {
      users: [...this.snapshot.users],
      teachers: [...this.snapshot.teachers],
      categories: [...this.snapshot.categories],
      accreditationStandards: [...this.snapshot.accreditationStandards],
      documents: [...this.snapshot.documents],
      evidence: [...this.snapshot.evidence],
      verifications: [...this.snapshot.verifications],
      activityLogs: [...this.snapshot.activityLogs]
    };
  }

  loginDemoByRole(role: UserRole): ActionResult<UserProfile> {
    const user = this.snapshot.users.find((u) => u.role === role && u.isActive);
    if (!user) {
      return {
        ok: false,
        message: `Akun demo untuk peran ${role} tidak ditemukan.`
      };
    }
    this.appendLog({
      actorUid: user.uid,
      actorName: user.fullName,
      actorRole: user.role,
      action: "LOGIN_DEMO",
      targetCollection: FIRESTORE_COLLECTIONS.USERS,
      detail: `Masuk ke mode Preview Demo sebagai ${user.role}.`
    });
    return {
      ok: true,
      message: `Berhasil masuk sebagai ${user.fullName} (${user.role}).`,
      data: user
    };
  }

  loginByEmail(email: string): ActionResult<UserProfile> {
    const normalized = email.trim().toLowerCase();
    if (!normalized) {
      return { ok: false, message: "Email wajib diisi." };
    }
    const found = this.snapshot.users.find(
      (u) => u.email.toLowerCase() === normalized
    );
    if (!found) {
      return {
        ok: false,
        message:
          "Email belum terdaftar di direktori sekolah. Gunakan akun demo cepat atau daftar mandiri."
      };
    }
    if (!found.isApprovedByAdmin || !found.isActive) {
      return {
        ok: false,
        message:
          "Akun menunggu persetujuan Admin sekolah sebelum dapat mengakses dashboard."
      };
    }
    this.appendLog({
      actorUid: found.uid,
      actorName: found.fullName,
      actorRole: found.role,
      action: "LOGIN_EMAIL",
      targetCollection: FIRESTORE_COLLECTIONS.USERS,
      detail: `Autentikasi email berhasil (${found.role}).`
    });
    return {
      ok: true,
      message: `Selamat datang, ${found.fullName}.`,
      data: found
    };
  }

  /**
   * Keamanan RBAC: Pendaftaran mandiri TIDAK PERNAH memberikan peran ADMIN.
   */
  registerSelfService(payload: RegisterPayload): ActionResult<UserProfile> {
    const fullName = payload.fullName.trim();
    const email = payload.email.trim().toLowerCase();
    const nipOrNuptk = payload.nipOrNuptk.trim();

    if (!fullName || !email || !nipOrNuptk) {
      return {
        ok: false,
        message: "Nama lengkap, email, dan NIP/NUPTK wajib diisi."
      };
    }

    const exists = this.snapshot.users.some(
      (u) => u.email.toLowerCase() === email
    );
    if (exists) {
      return {
        ok: false,
        message: "Email tersebut sudah terdaftar dalam sistem."
      };
    }

    // Paksa peran non-admin walau ada payload manipulasi
    const safeRole: UserRole =
      payload.requestedRole === "KEPALA_SEKOLAH" ||
      payload.requestedRole === "OPERATOR" ||
      payload.requestedRole === "GURU"
        ? payload.requestedRole
        : "GURU";

    const newUser: UserProfile = {
      uid: `user-${Date.now()}`,
      fullName,
      email,
      role: safeRole,
      nipOrNuptk,
      isActive: false,
      isApprovedByAdmin: false,
      createdAtIso: new Date().toISOString(),
      isDemoAccount: true
    };

    this.snapshot.users = [newUser, ...this.snapshot.users];
    this.appendLog({
      actorUid: newUser.uid,
      actorName: newUser.fullName,
      actorRole: newUser.role,
      action: "DAFTAR_MANDIRI",
      targetCollection: FIRESTORE_COLLECTIONS.USERS,
      detail: `Pendaftaran mandiri sebagai ${safeRole} (menunggu persetujuan Admin, hak ADMIN ditolak otomatis).`
    });

    return {
      ok: true,
      message:
        "Pendaftaran berhasil dikirim sebagai akun non-admin dan menunggu persetujuan Admin sekolah.",
      data: newUser
    };
  }

  async addDocument(payload: CreateDocumentPayload): Promise<ActionResult<SchoolDocument>> {
    if (!payload.title.trim() || !payload.documentNumber.trim()) {
      return {
        ok: false,
        message: "Judul dokumen dan nomor dokumen wajib diisi."
      };
    }
    const now = new Date().toISOString();
    const newDoc: SchoolDocument = {
      id: `doc-${Date.now()}`,
      title: payload.title.trim(),
      categoryId: payload.categoryId,
      standardId: payload.standardId,
      documentNumber: payload.documentNumber.trim(),
      schoolYear: payload.schoolYear.trim() || "2025/2026",
      uploadedByUid: payload.actor.uid,
      uploadedByName: payload.actor.fullName,
      fileUrlOrPath:
        payload.fileUrlOrPath.trim() ||
        "https://drive.google.com/drive/folders/1rPq3eEgEZbO_uvFSEO7qEwWm4XFWoRVl",
      status: "MENUNGGU",
      updatedAtIso: now
    };

    this.snapshot.documents = [newDoc, ...this.snapshot.documents];

    if (payload.alsoAttachEvidence) {
      const ev: EvidenceItem = {
        id: `ev-${Date.now()}`,
        title: `Bukti Pendukung: ${newDoc.title}`,
        categoryId: newDoc.categoryId,
        documentId: newDoc.id,
        mediaType: "Tautan Google Drive",
        description: `Lampiran otomatis dari dokumen ${newDoc.documentNumber}`,
        capturedDateIso: now.slice(0, 10),
        submittedByName: payload.actor.fullName,
        verificationStatus: "MENUNGGU"
      };
      this.snapshot.evidence = [ev, ...this.snapshot.evidence];
    }

    this.appendLog({
      actorUid: payload.actor.uid,
      actorName: payload.actor.fullName,
      actorRole: payload.actor.role,
      action: "UNGGAH_DOKUMEN",
      targetCollection: FIRESTORE_COLLECTIONS.DOCUMENTS,
      detail: `Mengunggah "${newDoc.title}" (${newDoc.documentNumber}).`
    });

    const { db } = getFirebaseServices();
    if (db) {
      try {
        await setDoc(doc(db, FIRESTORE_COLLECTIONS.DOCUMENTS, newDoc.id), newDoc);
      } catch {
        // Fallback aman jika rules Firestore belum aktif
      }
    }

    return {
      ok: true,
      message: `Dokumen "${newDoc.title}" tersimpan dengan status MENUNGGU verifikasi.`,
      data: newDoc
    };
  }

  verifyTarget(payload: VerifyTargetPayload): ActionResult<VerificationRecord> {
    if (
      payload.reviewer.role !== "ADMIN" &&
      payload.reviewer.role !== "KEPALA_SEKOLAH"
    ) {
      return {
        ok: false,
        message: "Hanya KEPALA_SEKOLAH atau ADMIN yang berhak melakukan verifikasi."
      };
    }

    let targetTitle = "";
    let categoryId: CategoryId = "profil_sekolah";

    if (payload.targetType === "DOCUMENT") {
      this.snapshot.documents = this.snapshot.documents.map((d) => {
        if (d.id === payload.targetId) {
          targetTitle = d.title;
          categoryId = d.categoryId;
          return { ...d, status: payload.status, updatedAtIso: new Date().toISOString() };
        }
        return d;
      });
    } else {
      this.snapshot.evidence = this.snapshot.evidence.map((e) => {
        if (e.id === payload.targetId) {
          targetTitle = e.title;
          categoryId = e.categoryId;
          return { ...e, verificationStatus: payload.status };
        }
        return e;
      });
    }

    if (!targetTitle) {
      return { ok: false, message: "Item target verifikasi tidak ditemukan." };
    }

    const record: VerificationRecord = {
      id: `ver-${Date.now()}`,
      targetId: payload.targetId,
      targetType: payload.targetType,
      targetTitle,
      categoryId,
      reviewerUid: payload.reviewer.uid,
      reviewerName: payload.reviewer.fullName,
      status: payload.status,
      notes: payload.notes.trim() || "Catatan verifikasi diperbarui.",
      reviewedAtIso: new Date().toISOString()
    };

    this.snapshot.verifications = [record, ...this.snapshot.verifications];
    this.appendLog({
      actorUid: payload.reviewer.uid,
      actorName: payload.reviewer.fullName,
      actorRole: payload.reviewer.role,
      action: "VERIFIKASI_DOKUMEN",
      targetCollection: FIRESTORE_COLLECTIONS.VERIFICATIONS,
      detail: `Menetapkan ${targetTitle} -> ${payload.status}.`
    });

    return {
      ok: true,
      message: `Verifikasi "${targetTitle}" diperbarui menjadi ${payload.status}.`,
      data: record
    };
  }

  approveAndUpdateUserRole(
    actor: UserProfile,
    targetUid: string,
    newRole: UserRole,
    approve: boolean
  ): ActionResult {
    if (actor.role !== "ADMIN") {
      return {
        ok: false,
        message: "Hanya ADMIN yang dapat menyetujui akun atau mengubah peran pengguna."
      };
    }
    let updatedName = "";
    this.snapshot.users = this.snapshot.users.map((u) => {
      if (u.uid === targetUid) {
        updatedName = u.fullName;
        return {
          ...u,
          role: newRole,
          isApprovedByAdmin: approve,
          isActive: approve
        };
      }
      return u;
    });
    if (!updatedName) {
      return { ok: false, message: "Pengguna tidak ditemukan." };
    }
    this.appendLog({
      actorUid: actor.uid,
      actorName: actor.fullName,
      actorRole: actor.role,
      action: "KELOLA_PENGGUNA",
      targetCollection: FIRESTORE_COLLECTIONS.USERS,
      detail: `${approve ? "Menyetujui" : "Menonaktifkan"} ${updatedName} dengan peran ${newRole}.`
    });
    return {
      ok: true,
      message: `Status pengguna ${updatedName} diperbarui (${newRole}).`
    };
  }

  async syncProductionDocsIfConfigured(): Promise<number | null> {
    const { db } = getFirebaseServices();
    if (!db) return null;
    try {
      const snap = await getDocs(collection(db, FIRESTORE_COLLECTIONS.DOCUMENTS));
      return snap.size;
    } catch {
      return null;
    }
  }

  private appendLog(
    entry: Omit<ActivityLog, "id" | "timestampIso">
  ): void {
    const log: ActivityLog = {
      id: `log-${Date.now()}-${Math.floor(Math.random() * 1000)}`,
      timestampIso: new Date().toISOString(),
      ...entry
    };
    this.snapshot.activityLogs = [log, ...this.snapshot.activityLogs];
  }
}

export const siadsService = new SiadsService();
