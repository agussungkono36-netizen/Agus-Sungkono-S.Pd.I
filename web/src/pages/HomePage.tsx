import React from "react";
import { useNavigate } from "react-router-dom";
import {
  Database,
  ExternalLink,
  FolderOpen,
  LayoutDashboard,
  LogIn
} from "lucide-react";
import { CategoryButton } from "../components/CategoryButton";
import { SchoolHeader } from "../components/SchoolHeader";
import { SCHOOL_DOMAIN_CONFIG } from "../config/domain";
import { GOOGLE_DRIVE_CONFIG } from "../config/googleDrive";
import { useSiadsStore } from "../hooks/useSiadsStore";
import { CATEGORIES_META } from "../types/siads";

export const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const { snapshot, currentUser, firebaseStatus } = useSiadsStore();

  return (
    <div className="min-h-screen bg-slate-100">
      <div className="mx-auto max-w-5xl px-4 py-5 sm:px-6 sm:py-8">
        <SchoolHeader
          onLoginClick={() => navigate(currentUser ? "/dashboard" : "/login")}
          loggedInLabel={
            currentUser ? `Dashboard (${currentUser.role})` : "Login Portal"
          }
        />

        {/* Strip status sistem & domain */}
        <section className="mt-5 flex flex-wrap items-center justify-between gap-3 rounded-2xl border border-slate-200 bg-white px-4 py-3 shadow-sm">
          <div className="flex items-center gap-2.5 text-xs sm:text-sm">
            <Database
              className={`h-4 w-4 shrink-0 ${
                firebaseStatus.isConfigured ? "text-emerald-600" : "text-amber-600"
              }`}
            />
            <span className="font-semibold text-slate-800">
              {firebaseStatus.statusMessage}
            </span>
          </div>

          <div className="flex flex-wrap items-center gap-2">
            <a
              href={GOOGLE_DRIVE_CONFIG.targetFolderUrl}
              target="_blank"
              rel="noreferrer"
              className="inline-flex items-center gap-1.5 rounded-xl border border-slate-200 bg-slate-50 px-3 py-1.5 text-xs font-bold text-slate-700 hover:bg-slate-100"
            >
              <FolderOpen className="h-3.5 w-3.5 text-blue-600" />
              Drive Arsip Sekolah
              <ExternalLink className="h-3 w-3" />
            </a>
            <button
              type="button"
              onClick={() => navigate(currentUser ? "/dashboard" : "/login")}
              className="inline-flex items-center gap-1.5 rounded-xl bg-navy px-3.5 py-1.5 text-xs font-bold text-white hover:bg-slate-800"
            >
              {currentUser ? (
                <>
                  <LayoutDashboard className="h-3.5 w-3.5" />
                  Buka Dashboard
                </>
              ) : (
                <>
                  <LogIn className="h-3.5 w-3.5" />
                  Masuk / Daftar
                </>
              )}
            </button>
          </div>
        </section>

        {/* 5 Tombol Kategori Utama */}
        <section className="mt-6">
          <div className="mb-3 flex items-end justify-between px-1">
            <div>
              <h2 className="text-lg font-extrabold text-slate-900 sm:text-xl">
                Kategori Akreditasi & Dokumentasi
              </h2>
              <p className="text-xs text-slate-600 sm:text-sm">
                Pilih bidang untuk melihat standar, dokumen resmi, dan unggahan bukti fisik.
              </p>
            </div>
            <span className="hidden text-xs font-semibold text-slate-500 sm:inline">
              {snapshot.documents.length} Total Dokumen Preview
            </span>
          </div>

          <div className="grid gap-3.5">
            {CATEGORIES_META.map((cat) => {
              const count = snapshot.documents.filter(
                (d) => d.categoryId === cat.id
              ).length;
              return (
                <CategoryButton
                  key={cat.id}
                  category={cat}
                  documentCount={count}
                  onClick={() => navigate(`/kategori/${cat.id}`)}
                />
              );
            })}
          </div>
        </section>

        <footer className="mt-8 border-t border-slate-200 pt-5 text-center text-xs text-slate-500">
          <p className="font-semibold text-slate-700">
            SIADS {SCHOOL_DOMAIN_CONFIG.schoolName} · Domain Resmi:{" "}
            <a
              href={SCHOOL_DOMAIN_CONFIG.baseUrl}
              target="_blank"
              rel="noreferrer"
              className="text-blue-700 hover:underline"
            >
              {SCHOOL_DOMAIN_CONFIG.primaryDomain}
            </a>
          </p>
          <p className="mt-1">
            Tahap Pertama Aplikasi Produksi · Data demo terisolasi dari database Firestore produksi.
          </p>
        </footer>
      </div>
    </div>
  );
};
