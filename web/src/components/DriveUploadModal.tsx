import React, { useState } from "react";
import { CloudUpload, ExternalLink, FileUp, FolderOpen, X } from "lucide-react";
import { GOOGLE_DRIVE_CONFIG } from "../config/googleDrive";
import { useSiadsStore } from "../hooks/useSiadsStore";
import { CATEGORIES_META, type CategoryId } from "../types/siads";

interface DriveUploadModalProps {
  isOpen: boolean;
  defaultCategoryId?: CategoryId;
  onClose: () => void;
}

export const DriveUploadModal: React.FC<DriveUploadModalProps> = ({
  isOpen,
  defaultCategoryId = "pengembangan_diri",
  onClose
}) => {
  const {
    currentUser,
    snapshot,
    driveAccessToken,
    isDriveBusy,
    connectGoogleDrive,
    uploadToGoogleDrive,
    addDocument
  } = useSiadsStore();

  const [categoryId, setCategoryId] = useState<CategoryId>(defaultCategoryId);
  const [title, setTitle] = useState("");
  const [documentNumber, setDocumentNumber] = useState("");
  const [schoolYear, setSchoolYear] = useState("2025/2026");
  const [fileUrlOrPath, setFileUrlOrPath] = useState("");
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [attachEvidence, setAttachEvidence] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  if (!isOpen) return null;

  const standards = snapshot.accreditationStandards.filter(
    (s) => s.categoryId === categoryId
  );
  const activeStandardId = standards[0]?.id || "std-pd-01";

  const handleUploadSelectedFileToDrive = async () => {
    if (!selectedFile) return;
    const uploaded = await uploadToGoogleDrive(
      selectedFile,
      `${title || selectedFile.name} — Kategori ${categoryId}`
    );
    if (uploaded) {
      setFileUrlOrPath(uploaded.webViewLink);
      if (!title.trim()) {
        setTitle(uploaded.name.replace(/\.[^/.]+$/, ""));
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      let finalUrl = fileUrlOrPath.trim();
      if (selectedFile && !finalUrl && driveAccessToken) {
        const uploaded = await uploadToGoogleDrive(
          selectedFile,
          `${title || selectedFile.name} — Kategori ${categoryId}`
        );
        if (uploaded) {
          finalUrl = uploaded.webViewLink;
        }
      }

      const res = await addDocument({
        title,
        categoryId,
        standardId: activeStandardId,
        documentNumber,
        schoolYear,
        fileUrlOrPath: finalUrl || GOOGLE_DRIVE_CONFIG.targetFolderUrl,
        alsoAttachEvidence: attachEvidence
      });
      if (res.ok) {
        setTitle("");
        setDocumentNumber("");
        setFileUrlOrPath("");
        setSelectedFile(null);
        onClose();
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/60 p-4 backdrop-blur-sm"
      role="dialog"
      aria-modal="true"
    >
      <div className="max-h-[90vh] w-full max-w-xl overflow-y-auto rounded-2xl bg-white p-6 shadow-2xl">
        <div className="flex items-start justify-between gap-3 border-b border-slate-200 pb-4">
          <div>
            <h3 className="text-lg font-extrabold text-slate-900">
              Unggah Dokumen & Simpan ke Google Drive
            </h3>
            <p className="mt-1 text-xs text-slate-600">
              Folder tujuan resmi sekolah:{" "}
              <a
                href={GOOGLE_DRIVE_CONFIG.targetFolderUrl}
                target="_blank"
                rel="noreferrer"
                className="inline-flex items-center gap-1 font-semibold text-blue-700 hover:underline"
              >
                Drive Folder {GOOGLE_DRIVE_CONFIG.targetFolderId.slice(0, 10)}...
                <ExternalLink className="h-3 w-3" />
              </a>
            </p>
          </div>
          <button
            type="button"
            onClick={onClose}
            aria-label="Tutup modal unggah"
            className="rounded-lg p-1.5 text-slate-500 hover:bg-slate-100"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="mt-4 space-y-4">
          <div className="rounded-xl border border-blue-200 bg-blue-50/70 p-3.5">
            <div className="flex flex-wrap items-center justify-between gap-2">
              <div className="text-xs text-blue-950">
                <p className="font-bold">Integrasi Google Drive Langsung</p>
                <p className="text-blue-800">
                  {driveAccessToken
                    ? "OAuth Drive aktif — berkas yang dipilih akan diunggah langsung ke folder tujuan."
                    : "Hubungkan OAuth Google Drive (membutuhkan VITE_GOOGLE_DRIVE_CLIENT_ID) atau tempel tautan Drive."}
                </p>
              </div>
              <button
                type="button"
                disabled={isDriveBusy}
                onClick={() => void connectGoogleDrive()}
                className="inline-flex items-center gap-1.5 rounded-lg bg-blue-700 px-3 py-1.5 text-xs font-bold text-white hover:bg-blue-800 disabled:opacity-50"
              >
                <FolderOpen className="h-4 w-4" />
                {driveAccessToken ? "Drive Terhubung" : "Hubungkan Drive"}
              </button>
            </div>

            <div className="mt-3 flex flex-wrap items-center gap-2">
              <input
                type="file"
                onChange={(e) => setSelectedFile(e.target.files?.[0] || null)}
                className="block w-full text-xs text-slate-700 file:mr-3 file:rounded-lg file:border-0 file:bg-white file:px-3 file:py-1.5 file:text-xs file:font-semibold file:text-slate-800 hover:file:bg-slate-100 sm:w-auto"
              />
              {selectedFile && driveAccessToken && (
                <button
                  type="button"
                  disabled={isDriveBusy}
                  onClick={() => void handleUploadSelectedFileToDrive()}
                  className="inline-flex items-center gap-1 rounded-lg bg-emerald-600 px-3 py-1.5 text-xs font-bold text-white hover:bg-emerald-700 disabled:opacity-50"
                >
                  <FileUp className="h-3.5 w-3.5" />
                  Kirim ke Folder Drive Sekarang
                </button>
              )}
            </div>
          </div>

          <div className="grid gap-3 sm:grid-cols-2">
            <div>
              <label className="mb-1 block text-xs font-bold text-slate-700">
                Kategori Akreditasi
              </label>
              <select
                value={categoryId}
                onChange={(e) => setCategoryId(e.target.value as CategoryId)}
                className="w-full rounded-xl border border-slate-300 px-3 py-2 text-sm focus:border-navy focus:outline-none"
              >
                {CATEGORIES_META.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.title}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="mb-1 block text-xs font-bold text-slate-700">
                Tahun Ajaran
              </label>
              <input
                type="text"
                value={schoolYear}
                onChange={(e) => setSchoolYear(e.target.value)}
                required
                className="w-full rounded-xl border border-slate-300 px-3 py-2 text-sm focus:border-navy focus:outline-none"
              />
            </div>
          </div>

          <div>
            <label className="mb-1 block text-xs font-bold text-slate-700">
              Judul Dokumen / SK / Laporan
            </label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="Contoh: SK Pembina Ekstrakurikuler Pramuka 2026"
              required
              className="w-full rounded-xl border border-slate-300 px-3 py-2 text-sm focus:border-navy focus:outline-none"
            />
          </div>

          <div>
            <label className="mb-1 block text-xs font-bold text-slate-700">
              Nomor Dokumen Resmi
            </label>
            <input
              type="text"
              value={documentNumber}
              onChange={(e) => setDocumentNumber(e.target.value)}
              placeholder="Contoh: 421.2/045/SDN1N/2026"
              required
              className="w-full rounded-xl border border-slate-300 px-3 py-2 text-sm focus:border-navy focus:outline-none"
            />
          </div>

          <div>
            <label className="mb-1 block text-xs font-bold text-slate-700">
              Tautan Google Drive / Path Arsip
            </label>
            <input
              type="url"
              value={fileUrlOrPath}
              onChange={(e) => setFileUrlOrPath(e.target.value)}
              placeholder={GOOGLE_DRIVE_CONFIG.targetFolderUrl}
              className="w-full rounded-xl border border-slate-300 px-3 py-2 text-sm focus:border-navy focus:outline-none"
            />
          </div>

          <label className="flex items-center gap-2 text-xs font-medium text-slate-700">
            <input
              type="checkbox"
              checked={attachEvidence}
              onChange={(e) => setAttachEvidence(e.target.checked)}
              className="h-4 w-4 rounded border-slate-300 text-navy"
            />
            Catat otomatis ke koleksi bukti fisik (evidence)
          </label>

          {!currentUser && (
            <p className="rounded-lg bg-amber-50 p-2.5 text-xs font-medium text-amber-800">
              Anda belum login. Masuk terlebih dahulu di halaman Login untuk menyimpan dokumen.
            </p>
          )}

          <div className="flex justify-end gap-2 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="rounded-xl border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50"
            >
              Batal
            </button>
            <button
              type="submit"
              disabled={!currentUser || submitting || isDriveBusy}
              className="inline-flex items-center gap-2 rounded-xl bg-navy px-4 py-2 text-sm font-bold text-white hover:bg-slate-800 disabled:opacity-50"
            >
              <CloudUpload className="h-4 w-4" />
              {submitting ? "Menyimpan..." : "Simpan Dokumen"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
