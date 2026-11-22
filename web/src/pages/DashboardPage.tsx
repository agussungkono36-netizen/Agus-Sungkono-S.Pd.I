import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  CheckCircle2,
  CloudUpload,
  FileText,
  Lock,
  ShieldCheck,
  Users
} from "lucide-react";
import { DriveUploadModal } from "../components/DriveUploadModal";
import { RoleBadge, StatusBadge } from "../components/RoleBadge";
import { useSiadsStore } from "../hooks/useSiadsStore";
import { InternalSidebarLayout } from "../layouts/InternalSidebarLayout";
import { CATEGORIES_META } from "../types/siads";

export const DashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const { currentUser, snapshot } = useSiadsStore();
  const [uploadOpen, setUploadOpen] = useState(false);

  if (!currentUser) {
    return (
      <InternalSidebarLayout pageTitle="Dashboard Peran Pengguna">
        <div className="rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm">
          <Lock className="mx-auto h-10 w-10 text-amber-600" />
          <h2 className="mt-3 text-lg font-extrabold text-slate-900">
            Autentikasi Diperlukan
          </h2>
          <p className="mx-auto mt-1 max-w-md text-sm text-slate-600">
            Masuk terlebih dahulu untuk melihat dashboard khusus peran ADMIN, KEPALA
            SEKOLAH, GURU, atau OPERATOR.
          </p>
          <button
            type="button"
            onClick={() => navigate("/login")}
            className="mt-5 rounded-xl bg-navy px-5 py-2.5 text-sm font-bold text-white hover:bg-slate-800"
          >
            Ke Halaman Login
          </button>
        </div>
      </InternalSidebarLayout>
    );
  }

  const waitingDocs = snapshot.documents.filter((d) => d.status === "MENUNGGU");
  const approvedDocs = snapshot.documents.filter((d) => d.status === "DISETUJUI");
  const revisionDocs = snapshot.documents.filter((d) => d.status === "PERLU_REVISI");
  const pendingUsers = snapshot.users.filter((u) => !u.isApprovedByAdmin);

  const roleHighlight: Record<typeof currentUser.role, string> = {
    ADMIN:
      "Kontrol penuh tata kelola pengguna, persetujuan pendaftar baru, struktur kategori, serta audit sistem.",
    KEPALA_SEKOLAH:
      "Tinjau kelengkapan dokumen akreditasi, beri persetujuan atau catatan revisi, serta pantau capaian standar.",
    GURU:
      "Unggah perangkat pembelajaran (KSP/Modul Ajar), dokumentasi ekstrakurikuler, dan bukti fisik kategori.",
    OPERATOR:
      "Kelola administrasi RKAS/BOS, data GTK sekolah, surat dinas, serta pengarsipan folder Google Drive."
  };

  return (
    <InternalSidebarLayout
      pageTitle={`Dashboard ${currentUser.role.replace("_", " ")}`}
      pageSubtitle={`Selamat bertugas, ${currentUser.fullName}`}
      headerAction={
        <button
          type="button"
          onClick={() => setUploadOpen(true)}
          className="inline-flex items-center gap-1.5 rounded-xl bg-navy px-3.5 py-2 text-xs font-bold text-white hover:bg-slate-800"
        >
          <CloudUpload className="h-4 w-4" />
          Unggah ke Drive
        </button>
      }
    >
      {/* Kartu Sambutan Peran */}
      <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <div className="flex items-center gap-2">
              <RoleBadge role={currentUser.role} />
              <span className="text-xs font-semibold text-slate-500">
                NIP/NUPTK: {currentUser.nipOrNuptk}
              </span>
            </div>
            <h2 className="mt-2 text-xl font-extrabold text-slate-900">
              {currentUser.fullName}
            </h2>
            <p className="mt-1 text-sm text-slate-600">
              {roleHighlight[currentUser.role]}
            </p>
          </div>
          <div className="flex flex-wrap gap-2">
            {(currentUser.role === "KEPALA_SEKOLAH" ||
              currentUser.role === "ADMIN") && (
              <button
                type="button"
                onClick={() => navigate("/verifikasi")}
                className="inline-flex items-center gap-1.5 rounded-xl bg-indigo-600 px-3.5 py-2 text-xs font-bold text-white hover:bg-indigo-700"
              >
                <CheckCircle2 className="h-4 w-4" />
                Antrean Verifikasi ({waitingDocs.length})
              </button>
            )}
            {currentUser.role === "ADMIN" && (
              <button
                type="button"
                onClick={() => navigate("/pengguna")}
                className="inline-flex items-center gap-1.5 rounded-xl bg-rose-600 px-3.5 py-2 text-xs font-bold text-white hover:bg-rose-700"
              >
                <Users className="h-4 w-4" />
                Persetujuan Akun ({pendingUsers.length})
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Ringkasan KPI */}
      <div className="mt-5 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <div className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
          <p className="text-xs font-bold text-slate-500">Total Dokumen</p>
          <p className="mt-1 text-2xl font-extrabold text-slate-900">
            {snapshot.documents.length}
          </p>
          <p className="mt-1 text-xs text-slate-500">5 kategori akreditasi</p>
        </div>
        <div className="rounded-2xl border border-emerald-200 bg-emerald-50/70 p-4 shadow-sm">
          <p className="text-xs font-bold text-emerald-700">Disetujui</p>
          <p className="mt-1 text-2xl font-extrabold text-emerald-900">
            {approvedDocs.length}
          </p>
          <p className="mt-1 text-xs text-emerald-700">Terverifikasi siap visitasi</p>
        </div>
        <div className="rounded-2xl border border-amber-200 bg-amber-50/70 p-4 shadow-sm">
          <p className="text-xs font-bold text-amber-800">Menunggu Verifikasi</p>
          <p className="mt-1 text-2xl font-extrabold text-amber-950">
            {waitingDocs.length}
          </p>
          <p className="mt-1 text-xs text-amber-800">
            Revisi diperlukan: {revisionDocs.length}
          </p>
        </div>
        <div className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
          <p className="text-xs font-bold text-slate-500">Data GTK Aktif</p>
          <p className="mt-1 text-2xl font-extrabold text-slate-900">
            {snapshot.teachers.length}
          </p>
          <p className="mt-1 text-xs text-slate-500">
            Pengguna sistem: {snapshot.users.length}
          </p>
        </div>
      </div>

      {/* Progres 5 Kategori */}
      <section className="mt-6 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <div className="flex items-center justify-between">
          <h3 className="text-base font-extrabold text-slate-900">
            Progres Kelengkapan 5 Kategori
          </h3>
          <button
            type="button"
            onClick={() => navigate("/dokumen")}
            className="text-xs font-bold text-blue-700 hover:underline"
          >
            Lihat semua dokumen →
          </button>
        </div>

        <div className="mt-4 grid gap-3 md:grid-cols-2">
          {CATEGORIES_META.map((cat) => {
            const catItem = snapshot.categories.find((c) => c.id === cat.id);
            const count = snapshot.documents.filter(
              (d) => d.categoryId === cat.id
            ).length;
            const target = catItem?.targetDocumentCount || 10;
            const pct = Math.min(100, Math.round((count / target) * 100));
            return (
              <button
                key={cat.id}
                type="button"
                onClick={() => navigate(`/kategori/${cat.id}`)}
                className="rounded-xl border border-slate-200 p-4 text-left transition hover:border-slate-400"
              >
                <div className="flex items-center justify-between gap-2">
                  <span className="text-sm font-extrabold text-slate-900">
                    {cat.title}
                  </span>
                  <span className="text-xs font-bold text-slate-600">
                    {count} / {target}
                  </span>
                </div>
                <div className="mt-2 h-2.5 w-full overflow-hidden rounded-full bg-slate-100">
                  <div
                    className="h-full rounded-full"
                    style={{ width: `${pct}%`, backgroundColor: cat.accentHex }}
                  />
                </div>
              </button>
            );
          })}
        </div>
      </section>

      {/* Dokumen Terbaru & Data GTK */}
      <div className="mt-6 grid gap-6 lg:grid-cols-2">
        <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <div className="mb-3 flex items-center gap-2">
            <FileText className="h-5 w-5 text-navy" />
            <h3 className="text-base font-extrabold text-slate-900">
              Dokumen Terbaru
            </h3>
          </div>
          <div className="space-y-2.5">
            {snapshot.documents.slice(0, 4).map((doc) => (
              <div
                key={doc.id}
                className="flex items-center justify-between gap-3 rounded-xl border border-slate-100 bg-slate-50 p-3"
              >
                <div className="min-w-0">
                  <p className="truncate text-sm font-bold text-slate-900">
                    {doc.title}
                  </p>
                  <p className="text-xs text-slate-500">
                    {doc.documentNumber} · {doc.uploadedByName}
                  </p>
                </div>
                <StatusBadge status={doc.status} />
              </div>
            ))}
          </div>
        </section>

        <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <div className="mb-3 flex items-center gap-2">
            <ShieldCheck className="h-5 w-5 text-navy" />
            <h3 className="text-base font-extrabold text-slate-900">
              Direktori Guru & Tenaga Kependidikan
            </h3>
          </div>
          <div className="space-y-2.5">
            {snapshot.teachers.map((t) => (
              <div
                key={t.id}
                className="flex items-center justify-between gap-3 rounded-xl border border-slate-100 bg-slate-50 p-3"
              >
                <div>
                  <p className="text-sm font-bold text-slate-900">{t.fullName}</p>
                  <p className="text-xs text-slate-500">
                    {t.position} · {t.classAssigned}
                  </p>
                </div>
                <span className="rounded-lg bg-emerald-100 px-2 py-0.5 text-[11px] font-bold text-emerald-800">
                  {t.certificationStatus}
                </span>
              </div>
            ))}
          </div>
        </section>
      </div>

      <DriveUploadModal isOpen={uploadOpen} onClose={() => setUploadOpen(false)} />
    </InternalSidebarLayout>
  );
};
