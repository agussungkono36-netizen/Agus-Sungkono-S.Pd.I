import React, { useState } from "react";
import {
  Check,
  CloudUpload,
  Database,
  ExternalLink,
  FolderOpen,
  RefreshCw,
  RotateCcw,
  ShieldCheck
} from "lucide-react";
import { DriveUploadModal } from "../components/DriveUploadModal";
import { RoleBadge, StatusBadge } from "../components/RoleBadge";
import { GOOGLE_DRIVE_CONFIG } from "../config/googleDrive";
import { SCHOOL_PWA_MANIFEST } from "../config/pwaManifest";
import { useSiadsStore } from "../hooks/useSiadsStore";
import { InternalSidebarLayout } from "../layouts/InternalSidebarLayout";
import { FIRESTORE_SCHEMA_BLUEPRINTS } from "../services/firestoreSchema";
import { CATEGORIES_META, type CategoryId, type UserRole } from "../types/siads";

export const DocumentsPage: React.FC = () => {
  const {
    snapshot,
    currentUser,
    driveAccessToken,
    driveFiles,
    isDriveBusy,
    connectGoogleDrive,
    refreshDriveFiles
  } = useSiadsStore();

  const [filterCat, setFilterCat] = useState<"ALL" | CategoryId>("ALL");
  const [uploadOpen, setUploadOpen] = useState(false);

  const filteredDocs =
    filterCat === "ALL"
      ? snapshot.documents
      : snapshot.documents.filter((d) => d.categoryId === filterCat);

  return (
    <InternalSidebarLayout
      pageTitle="Arsip Dokumen & Google Drive"
      pageSubtitle={`Target Folder: ${GOOGLE_DRIVE_CONFIG.targetFolderId}`}
      headerAction={
        <button
          type="button"
          onClick={() => setUploadOpen(true)}
          className="inline-flex items-center gap-1.5 rounded-xl bg-navy px-3.5 py-2 text-xs font-bold text-white hover:bg-slate-800"
        >
          <CloudUpload className="h-4 w-4" />
          Unggah Berkas
        </button>
      }
    >
      {/* Panel Google Drive */}
      <div className="rounded-2xl border border-blue-200 bg-blue-50/70 p-4">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <h2 className="text-sm font-extrabold text-blue-950">
              Folder Penyimpanan Resmi Google Drive SDN 1 Ngantru
            </h2>
            <p className="mt-0.5 text-xs text-blue-800">
              Semua unggahan berkas terhubung ke folder ID{" "}
              <code className="rounded bg-white px-1.5 py-0.5 font-mono text-[11px]">
                {GOOGLE_DRIVE_CONFIG.targetFolderId}
              </code>
            </p>
          </div>
          <div className="flex flex-wrap items-center gap-2">
            <a
              href={GOOGLE_DRIVE_CONFIG.targetFolderUrl}
              target="_blank"
              rel="noreferrer"
              className="inline-flex items-center gap-1 rounded-xl border border-blue-300 bg-white px-3 py-1.5 text-xs font-bold text-blue-900 hover:bg-blue-100"
            >
              Buka di Google Drive
              <ExternalLink className="h-3.5 w-3.5" />
            </a>
            <button
              type="button"
              disabled={isDriveBusy}
              onClick={() =>
                void (driveAccessToken ? refreshDriveFiles() : connectGoogleDrive())
              }
              className="inline-flex items-center gap-1.5 rounded-xl bg-blue-700 px-3 py-1.5 text-xs font-bold text-white hover:bg-blue-800 disabled:opacity-50"
            >
              {driveAccessToken ? (
                <>
                  <RefreshCw className="h-3.5 w-3.5" />
                  Muat Ulang File Drive
                </>
              ) : (
                <>
                  <FolderOpen className="h-3.5 w-3.5" />
                  Hubungkan OAuth Drive
                </>
              )}
            </button>
          </div>
        </div>

        {driveFiles.length > 0 && (
          <div className="mt-3 grid gap-2 sm:grid-cols-2">
            {driveFiles.map((file) => (
              <a
                key={file.id}
                href={file.webViewLink}
                target="_blank"
                rel="noreferrer"
                className="flex items-center justify-between rounded-xl border border-blue-200 bg-white px-3 py-2 text-xs hover:bg-blue-50"
              >
                <span className="truncate font-semibold text-slate-800">
                  {file.name}
                </span>
                <ExternalLink className="h-3.5 w-3.5 shrink-0 text-blue-600" />
              </a>
            ))}
          </div>
        )}
      </div>

      {/* Filter Kategori */}
      <div className="mt-5 flex flex-wrap gap-2">
        <button
          type="button"
          onClick={() => setFilterCat("ALL")}
          className={`rounded-xl px-3 py-1.5 text-xs font-bold transition ${
            filterCat === "ALL"
              ? "bg-navy text-white"
              : "border border-slate-300 bg-white text-slate-700"
          }`}
        >
          Semua ({snapshot.documents.length})
        </button>
        {CATEGORIES_META.map((cat) => (
          <button
            key={cat.id}
            type="button"
            onClick={() => setFilterCat(cat.id)}
            className={`rounded-xl px-3 py-1.5 text-xs font-bold transition ${
              filterCat === cat.id
                ? "bg-navy text-white"
                : "border border-slate-300 bg-white text-slate-700"
            }`}
          >
            {cat.shortTitle}
          </button>
        ))}
      </div>

      <div className="mt-4 space-y-3">
        {filteredDocs.map((doc) => (
          <div
            key={doc.id}
            className="flex flex-col justify-between gap-3 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:flex-row sm:items-center"
          >
            <div>
              <div className="flex flex-wrap items-center gap-2">
                <span className="text-xs font-bold text-slate-500">
                  {doc.documentNumber}
                </span>
                <StatusBadge status={doc.status} />
              </div>
              <h3 className="mt-1 text-sm font-bold text-slate-900">{doc.title}</h3>
              <p className="text-xs text-slate-500">
                Pengunggah: {doc.uploadedByName} · TA {doc.schoolYear}
              </p>
            </div>
            <a
              href={doc.fileUrlOrPath}
              target="_blank"
              rel="noreferrer"
              className="inline-flex items-center gap-1.5 rounded-xl border border-slate-300 px-3 py-2 text-xs font-bold text-slate-700 hover:bg-slate-50"
            >
              Tautan Drive
              <ExternalLink className="h-3.5 w-3.5" />
            </a>
          </div>
        ))}
      </div>

      {!currentUser && (
        <p className="mt-4 text-xs text-slate-500">
          Catatan: Masuk di halaman Login untuk menambahkan entri dokumen baru.
        </p>
      )}

      <DriveUploadModal isOpen={uploadOpen} onClose={() => setUploadOpen(false)} />
    </InternalSidebarLayout>
  );
};

export const VerificationsPage: React.FC = () => {
  const { currentUser, snapshot, verifyTarget } = useSiadsStore();
  const [notesById, setNotesById] = useState<Record<string, string>>({});

  const canVerify =
    currentUser?.role === "ADMIN" || currentUser?.role === "KEPALA_SEKOLAH";

  return (
    <InternalSidebarLayout
      pageTitle="Verifikasi Dokumen Akreditasi"
      pageSubtitle="Hak verifikasi hanya untuk KEPALA_SEKOLAH dan ADMIN"
    >
      {!canVerify && (
        <div className="mb-4 rounded-2xl border border-amber-200 bg-amber-50 p-4 text-xs font-semibold text-amber-900">
          Mode baca: Masuk sebagai KEPALA_SEKOLAH atau ADMIN untuk menyetujui atau
          meminta revisi dokumen.
        </div>
      )}

      <div className="space-y-3">
        {snapshot.documents.map((doc) => (
          <div
            key={doc.id}
            className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm"
          >
            <div className="flex flex-wrap items-center justify-between gap-2">
              <div>
                <div className="flex items-center gap-2">
                  <span className="text-xs font-bold text-slate-500">
                    {doc.documentNumber}
                  </span>
                  <StatusBadge status={doc.status} />
                </div>
                <h3 className="mt-1 text-sm font-bold text-slate-900">
                  {doc.title}
                </h3>
                <p className="text-xs text-slate-500">
                  Oleh {doc.uploadedByName} · Kategori {doc.categoryId}
                </p>
              </div>
              <a
                href={doc.fileUrlOrPath}
                target="_blank"
                rel="noreferrer"
                className="inline-flex items-center gap-1 text-xs font-bold text-blue-700 hover:underline"
              >
                Periksa Berkas
                <ExternalLink className="h-3.5 w-3.5" />
              </a>
            </div>

            {canVerify && (
              <div className="mt-3 flex flex-col gap-2 border-t border-slate-100 pt-3 sm:flex-row">
                <input
                  type="text"
                  value={notesById[doc.id] || ""}
                  onChange={(e) =>
                    setNotesById((prev) => ({ ...prev, [doc.id]: e.target.value }))
                  }
                  placeholder="Catatan verifikasi Kepala Sekolah / Admin..."
                  className="flex-1 rounded-xl border border-slate-300 px-3 py-1.5 text-xs focus:border-navy focus:outline-none"
                />
                <div className="flex gap-2">
                  <button
                    type="button"
                    onClick={() =>
                      verifyTarget({
                        targetId: doc.id,
                        targetType: "DOCUMENT",
                        status: "DISETUJUI",
                        notes:
                          notesById[doc.id] || "Dokumen lengkap dan valid untuk akreditasi."
                      })
                    }
                    className="inline-flex items-center gap-1 rounded-xl bg-emerald-600 px-3 py-1.5 text-xs font-bold text-white hover:bg-emerald-700"
                  >
                    <Check className="h-3.5 w-3.5" />
                    Setujui
                  </button>
                  <button
                    type="button"
                    onClick={() =>
                      verifyTarget({
                        targetId: doc.id,
                        targetType: "DOCUMENT",
                        status: "PERLU_REVISI",
                        notes:
                          notesById[doc.id] || "Mohon lengkapi lampiran pengesahan."
                      })
                    }
                    className="inline-flex items-center gap-1 rounded-xl bg-rose-600 px-3 py-1.5 text-xs font-bold text-white hover:bg-rose-700"
                  >
                    <RotateCcw className="h-3.5 w-3.5" />
                    Revisi
                  </button>
                </div>
              </div>
            )}
          </div>
        ))}
      </div>

      <section className="mt-6">
        <h3 className="mb-3 text-base font-extrabold text-slate-900">
          Riwayat Verifikasi ({snapshot.verifications.length})
        </h3>
        <div className="space-y-2.5">
          {snapshot.verifications.map((ver) => (
            <div
              key={ver.id}
              className="rounded-xl border border-slate-200 bg-white p-3.5 text-xs"
            >
              <div className="flex flex-wrap items-center justify-between gap-2">
                <span className="font-bold text-slate-900">{ver.targetTitle}</span>
                <StatusBadge status={ver.status} />
              </div>
              <p className="mt-1 text-slate-600">{ver.notes}</p>
              <p className="mt-1 text-slate-400">
                Reviewer: {ver.reviewerName} · {ver.reviewedAtIso}
              </p>
            </div>
          ))}
        </div>
      </section>
    </InternalSidebarLayout>
  );
};

export const ActivityLogsPage: React.FC = () => {
  const { snapshot } = useSiadsStore();

  return (
    <InternalSidebarLayout
      pageTitle="Log Aktivitas SIADS"
      pageSubtitle="Jejak audit koleksi activityLogs"
    >
      <div className="space-y-3">
        {snapshot.activityLogs.map((log) => (
          <div
            key={log.id}
            className="flex flex-col justify-between gap-2 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:flex-row sm:items-center"
          >
            <div>
              <div className="flex flex-wrap items-center gap-2">
                <span className="rounded-lg bg-slate-100 px-2 py-0.5 text-[11px] font-extrabold text-slate-800">
                  {log.action}
                </span>
                <RoleBadge role={log.actorRole} />
                <span className="text-xs font-semibold text-slate-500">
                  Koleksi: {log.targetCollection}
                </span>
              </div>
              <p className="mt-1.5 text-sm font-bold text-slate-900">
                {log.actorName}
              </p>
              <p className="text-xs text-slate-600">{log.detail}</p>
            </div>
            <span className="text-xs font-medium text-slate-400">
              {log.timestampIso}
            </span>
          </div>
        ))}
      </div>
    </InternalSidebarLayout>
  );
};

export const UserManagementPage: React.FC = () => {
  const { currentUser, snapshot, approveAndUpdateUserRole } = useSiadsStore();
  const isAdmin = currentUser?.role === "ADMIN";

  return (
    <InternalSidebarLayout
      pageTitle="Manajemen Pengguna, Skema Firestore & PWA"
      pageSubtitle="8 koleksi awal Firestore + konfigurasi WebAppManifest"
    >
      <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <div className="mb-3 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <ShieldCheck className="h-5 w-5 text-navy" />
            <h2 className="text-base font-extrabold text-slate-900">
              Daftar Akun & Persetujuan Peran
            </h2>
          </div>
          {!isAdmin && (
            <span className="rounded-full bg-amber-100 px-3 py-1 text-[11px] font-bold text-amber-900">
              Masuk sebagai ADMIN untuk mengubah peran
            </span>
          )}
        </div>

        <div className="space-y-3">
          {snapshot.users.map((u) => (
            <div
              key={u.uid}
              className="flex flex-col justify-between gap-3 rounded-xl border border-slate-200 bg-slate-50 p-3.5 sm:flex-row sm:items-center"
            >
              <div>
                <div className="flex flex-wrap items-center gap-2">
                  <span className="text-sm font-bold text-slate-900">
                    {u.fullName}
                  </span>
                  <RoleBadge role={u.role} />
                  <span
                    className={`rounded-full px-2 py-0.5 text-[11px] font-bold ${
                      u.isApprovedByAdmin
                        ? "bg-emerald-100 text-emerald-800"
                        : "bg-amber-100 text-amber-800"
                    }`}
                  >
                    {u.isApprovedByAdmin ? "Disetujui" : "Menunggu Persetujuan"}
                  </span>
                </div>
                <p className="mt-0.5 text-xs text-slate-500">
                  {u.email} · NIP/NUPTK: {u.nipOrNuptk}
                </p>
              </div>

              {isAdmin && u.uid !== currentUser?.uid && (
                <div className="flex flex-wrap items-center gap-2">
                  <select
                    value={u.role}
                    onChange={(e) =>
                      approveAndUpdateUserRole(
                        u.uid,
                        e.target.value as UserRole,
                        u.isApprovedByAdmin
                      )
                    }
                    className="rounded-lg border border-slate-300 bg-white px-2.5 py-1.5 text-xs font-bold"
                  >
                    <option value="GURU">GURU</option>
                    <option value="OPERATOR">OPERATOR</option>
                    <option value="KEPALA_SEKOLAH">KEPALA_SEKOLAH</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                  <button
                    type="button"
                    onClick={() =>
                      approveAndUpdateUserRole(u.uid, u.role, !u.isApprovedByAdmin)
                    }
                    className={`rounded-lg px-3 py-1.5 text-xs font-bold text-white ${
                      u.isApprovedByAdmin
                        ? "bg-slate-600 hover:bg-slate-700"
                        : "bg-emerald-600 hover:bg-emerald-700"
                    }`}
                  >
                    {u.isApprovedByAdmin ? "Nonaktifkan" : "Setujui Akun"}
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      </section>

      {/* 8 Koleksi Awal Firestore */}
      <section className="mt-6">
        <div className="mb-3 flex items-center gap-2">
          <Database className="h-5 w-5 text-navy" />
          <h2 className="text-base font-extrabold text-slate-900">
            Struktur Awal 8 Koleksi Firestore
          </h2>
        </div>
        <div className="grid gap-3 md:grid-cols-2">
          {FIRESTORE_SCHEMA_BLUEPRINTS.map((schema) => (
            <div
              key={schema.collectionName}
              className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm"
            >
              <p className="font-mono text-sm font-extrabold text-navy">
                /{schema.collectionName}
              </p>
              <p className="mt-1 text-xs text-slate-600">{schema.purpose}</p>
              <p className="mt-2 text-[11px] font-semibold text-slate-700">
                Field: {schema.requiredFields.join(", ")}
              </p>
              <p className="mt-1 text-[11px] text-emerald-700">
                {schema.accessSummary}
              </p>
            </div>
          ))}
        </div>
      </section>

      {/* Konfigurasi PWA WebAppManifest */}
      <section className="mt-6 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 className="text-base font-extrabold text-slate-900">
          Konfigurasi PWA WebAppManifest
        </h2>
        <p className="mt-1 text-xs text-slate-600">
          Didefinisikan lewat interface TypeScript{" "}
          <code>WebAppManifest</code> dan disinkronkan ke{" "}
          <code>/public/manifest.webmanifest</code>.
        </p>
        <pre className="mt-3 overflow-x-auto rounded-xl bg-slate-950 p-4 text-xs text-emerald-300">
          {JSON.stringify(SCHOOL_PWA_MANIFEST, null, 2)}
        </pre>
      </section>
    </InternalSidebarLayout>
  );
};
