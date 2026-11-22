import React, { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  ArrowLeft,
  CloudUpload,
  ExternalLink,
  FileCheck2,
  FolderArchive,
  Layers
} from "lucide-react";
import { DriveUploadModal } from "../components/DriveUploadModal";
import { StatusBadge } from "../components/RoleBadge";
import { useSiadsStore } from "../hooks/useSiadsStore";
import { InternalSidebarLayout } from "../layouts/InternalSidebarLayout";
import { CATEGORIES_META, type CategoryId } from "../types/siads";

export const CategoryPage: React.FC = () => {
  const { categoryId } = useParams<{ categoryId: string }>();
  const navigate = useNavigate();
  const { snapshot, currentUser } = useSiadsStore();
  const [uploadOpen, setUploadOpen] = useState(false);

  const meta = CATEGORIES_META.find((c) => c.id === categoryId);

  if (!meta) {
    return (
      <InternalSidebarLayout pageTitle="Kategori Tidak Ditemukan">
        <div className="rounded-2xl border border-slate-200 bg-white p-6 text-center">
          <p className="text-sm font-semibold text-slate-700">
            Kategori "{categoryId}" tidak tersedia dalam SIADS.
          </p>
          <button
            type="button"
            onClick={() => navigate("/")}
            className="mt-4 inline-flex items-center gap-2 rounded-xl bg-navy px-4 py-2 text-sm font-bold text-white"
          >
            <ArrowLeft className="h-4 w-4" />
            Kembali ke Beranda
          </button>
        </div>
      </InternalSidebarLayout>
    );
  }

  const categoryDetail = snapshot.categories.find((c) => c.id === meta.id);
  const standards = snapshot.accreditationStandards.filter(
    (s) => s.categoryId === meta.id
  );
  const documents = snapshot.documents.filter((d) => d.categoryId === meta.id);
  const evidence = snapshot.evidence.filter((e) => e.categoryId === meta.id);

  return (
    <InternalSidebarLayout
      pageTitle={meta.title}
      pageSubtitle={`Kode Standar ${meta.standardCode} · Koordinator: ${
        categoryDetail?.coordinatorName || "-"
      }`}
      headerAction={
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={() => navigate("/")}
            className="inline-flex items-center gap-1.5 rounded-xl border border-slate-300 bg-white px-3 py-2 text-xs font-bold text-slate-700 hover:bg-slate-50"
          >
            <ArrowLeft className="h-4 w-4" />
            Beranda
          </button>
          <button
            type="button"
            onClick={() =>
              currentUser ? setUploadOpen(true) : navigate("/login")
            }
            className="inline-flex items-center gap-1.5 rounded-xl bg-navy px-3.5 py-2 text-xs font-bold text-white hover:bg-slate-800"
          >
            <CloudUpload className="h-4 w-4" />
            {currentUser ? "Unggah Dokumen" : "Login untuk Unggah"}
          </button>
        </div>
      }
    >
      {/* Hero Banner Kategori */}
      <div
        className="rounded-2xl p-6 text-white shadow-md"
        style={{ backgroundColor: meta.accentHex }}
      >
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div className="max-w-2xl">
            <span className="rounded-full bg-black/20 px-3 py-1 text-xs font-bold uppercase tracking-wider">
              {meta.standardCode}
            </span>
            <h2 className="mt-2 text-2xl font-extrabold">{meta.title}</h2>
            <p className="mt-1 text-sm opacity-95">
              {categoryDetail?.description || meta.subtitle}
            </p>
          </div>
          <div className="rounded-2xl bg-black/20 px-5 py-3 text-center">
            <p className="text-xs font-semibold opacity-90">Capaian Dokumen</p>
            <p className="text-2xl font-extrabold">
              {documents.length} / {categoryDetail?.targetDocumentCount || 10}
            </p>
          </div>
        </div>
      </div>

      {/* Standar Akreditasi */}
      <section className="mt-6">
        <div className="mb-3 flex items-center gap-2">
          <Layers className="h-5 w-5 text-navy" />
          <h3 className="text-base font-extrabold text-slate-900">
            Standar & Indikator Akreditasi
          </h3>
        </div>
        <div className="grid gap-3 md:grid-cols-2">
          {standards.map((std) => (
            <div
              key={std.id}
              className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm"
            >
              <div className="flex items-center justify-between gap-2">
                <span className="rounded-lg bg-slate-100 px-2.5 py-1 text-xs font-extrabold text-slate-700">
                  {std.code}
                </span>
                <span className="text-xs font-bold text-emerald-700">
                  Skor {std.weightScore} / {std.maxScore}
                </span>
              </div>
              <h4 className="mt-2 text-sm font-bold text-slate-900">{std.title}</h4>
              <p className="mt-1 text-xs leading-relaxed text-slate-600">
                {std.indicatorSummary}
              </p>
            </div>
          ))}
        </div>
      </section>

      {/* Daftar Dokumen */}
      <section className="mt-6">
        <div className="mb-3 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <FolderArchive className="h-5 w-5 text-navy" />
            <h3 className="text-base font-extrabold text-slate-900">
              Dokumen Terarsip ({documents.length})
            </h3>
          </div>
        </div>

        <div className="space-y-3">
          {documents.map((doc) => (
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
                <h4 className="mt-1 text-sm font-bold text-slate-900">
                  {doc.title}
                </h4>
                <p className="mt-0.5 text-xs text-slate-500">
                  Pengunggah: {doc.uploadedByName} · TA {doc.schoolYear}
                </p>
              </div>
              <a
                href={doc.fileUrlOrPath}
                target="_blank"
                rel="noreferrer"
                className="inline-flex shrink-0 items-center gap-1.5 rounded-xl border border-slate-300 px-3.5 py-2 text-xs font-bold text-slate-700 hover:bg-slate-50"
              >
                Buka Drive
                <ExternalLink className="h-3.5 w-3.5" />
              </a>
            </div>
          ))}
          {documents.length === 0 && (
            <div className="rounded-2xl border border-dashed border-slate-300 bg-white p-6 text-center text-sm text-slate-500">
              Belum ada dokumen pada kategori ini.
            </div>
          )}
        </div>
      </section>

      {/* Bukti Fisik */}
      <section className="mt-6">
        <div className="mb-3 flex items-center gap-2">
          <FileCheck2 className="h-5 w-5 text-navy" />
          <h3 className="text-base font-extrabold text-slate-900">
            Bukti Fisik & Dokumentasi ({evidence.length})
          </h3>
        </div>
        <div className="grid gap-3 md:grid-cols-2">
          {evidence.map((ev) => (
            <div
              key={ev.id}
              className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm"
            >
              <div className="flex items-center justify-between gap-2">
                <span className="text-xs font-bold text-slate-500">
                  {ev.mediaType} · {ev.capturedDateIso}
                </span>
                <StatusBadge status={ev.verificationStatus} />
              </div>
              <h4 className="mt-1.5 text-sm font-bold text-slate-900">{ev.title}</h4>
              <p className="mt-1 text-xs text-slate-600">{ev.description}</p>
            </div>
          ))}
        </div>
      </section>

      <DriveUploadModal
        isOpen={uploadOpen}
        defaultCategoryId={meta.id as CategoryId}
        onClose={() => setUploadOpen(false)}
      />
    </InternalSidebarLayout>
  );
};
