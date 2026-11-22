import React, { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import {
  Activity,
  CheckCircle2,
  Database,
  FileText,
  Home,
  LayoutDashboard,
  LogIn,
  LogOut,
  Menu,
  ShieldCheck,
  Users,
  X
} from "lucide-react";
import { SCHOOL_DOMAIN_CONFIG } from "../config/domain";
import { useSiadsStore } from "../hooks/useSiadsStore";
import { CATEGORIES_META } from "../types/siads";
import { RoleBadge } from "../components/RoleBadge";

interface InternalSidebarLayoutProps {
  pageTitle: string;
  pageSubtitle?: string;
  headerAction?: React.ReactNode;
  children: React.ReactNode;
}

export const InternalSidebarLayout: React.FC<InternalSidebarLayoutProps> = ({
  pageTitle,
  pageSubtitle,
  headerAction,
  children
}) => {
  const [mobileOpen, setMobileOpen] = useState(false);
  const { currentUser, firebaseStatus, feedback, clearFeedback, logout } =
    useSiadsStore();
  const navigate = useNavigate();

  const navLinkClass = ({ isActive }: { isActive: boolean }) =>
    `flex items-center gap-3 rounded-xl px-3.5 py-2.5 text-sm font-semibold transition ${
      isActive
        ? "bg-white text-navy shadow-sm"
        : "text-slate-200 hover:bg-white/10 hover:text-white"
    }`;

  const renderSidebarContent = (onNavigate?: () => void) => (
    <div className="flex h-full flex-col justify-between bg-navy p-4 text-white">
      <div className="space-y-5 overflow-y-auto pr-1">
        <div className="flex items-center gap-3 border-b border-white/15 pb-4">
          <img
            src={SCHOOL_DOMAIN_CONFIG.logoPath}
            alt="Logo SDN 1 Ngantru"
            className="h-11 w-11 rounded-full bg-white p-0.5 object-contain"
          />
          <div className="min-w-0">
            <p className="truncate text-sm font-extrabold tracking-wide">
              SIADS SDN 1 NGANTRU
            </p>
            <p className="truncate text-[11px] text-amber-300">
              {SCHOOL_DOMAIN_CONFIG.primaryDomain}
            </p>
          </div>
        </div>

        <nav className="space-y-1">
          <p className="px-2 pb-1 text-[10px] font-bold uppercase tracking-widest text-slate-400">
            Navigasi Utama
          </p>
          <NavLink to="/" end className={navLinkClass} onClick={onNavigate}>
            <Home className="h-4 w-4" />
            Beranda Sekolah
          </NavLink>
          <NavLink to="/dashboard" className={navLinkClass} onClick={onNavigate}>
            <LayoutDashboard className="h-4 w-4" />
            Dashboard Peran
          </NavLink>
          <NavLink to="/dokumen" className={navLinkClass} onClick={onNavigate}>
            <FileText className="h-4 w-4" />
            Arsip & Google Drive
          </NavLink>
          <NavLink to="/verifikasi" className={navLinkClass} onClick={onNavigate}>
            <CheckCircle2 className="h-4 w-4" />
            Verifikasi Akreditasi
          </NavLink>
          <NavLink to="/log-aktivitas" className={navLinkClass} onClick={onNavigate}>
            <Activity className="h-4 w-4" />
            Log Aktivitas
          </NavLink>
          <NavLink to="/pengguna" className={navLinkClass} onClick={onNavigate}>
            <Users className="h-4 w-4" />
            Pengguna & Skema DB
          </NavLink>
        </nav>

        <nav className="space-y-1">
          <p className="px-2 pb-1 text-[10px] font-bold uppercase tracking-widest text-slate-400">
            5 Kategori Akreditasi
          </p>
          {CATEGORIES_META.map((cat) => (
            <NavLink
              key={cat.id}
              to={`/kategori/${cat.id}`}
              onClick={onNavigate}
              className={({ isActive }) =>
                `flex items-center gap-2.5 rounded-xl px-3 py-2 text-xs font-semibold transition ${
                  isActive
                    ? "bg-white text-navy shadow-sm"
                    : "text-slate-200 hover:bg-white/10"
                }`
              }
            >
              <span
                className="h-2.5 w-2.5 shrink-0 rounded-full"
                style={{ backgroundColor: cat.accentHex }}
              />
              <span className="truncate">{cat.shortTitle}</span>
            </NavLink>
          ))}
        </nav>
      </div>

      <div className="mt-4 space-y-3 border-t border-white/15 pt-4">
        <div
          className={`rounded-xl px-3 py-2 text-[11px] font-semibold ${
            firebaseStatus.isConfigured
              ? "bg-emerald-500/20 text-emerald-200"
              : "bg-amber-500/20 text-amber-200"
          }`}
        >
          <div className="flex items-center gap-1.5">
            <Database className="h-3.5 w-3.5 shrink-0" />
            <span>
              {firebaseStatus.isConfigured
                ? "Firebase Terkonfigurasi"
                : "Mode Preview Demo"}
            </span>
          </div>
        </div>

        {currentUser ? (
          <div className="rounded-xl bg-white/10 p-3">
            <p className="truncate text-xs font-bold text-white">
              {currentUser.fullName}
            </p>
            <div className="mt-1.5 flex items-center justify-between gap-2">
              <RoleBadge role={currentUser.role} />
              <button
                type="button"
                onClick={() => {
                  logout();
                  onNavigate?.();
                }}
                className="inline-flex items-center gap-1 rounded-lg bg-white/15 px-2 py-1 text-[11px] font-semibold text-white hover:bg-white/25"
              >
                <LogOut className="h-3 w-3" />
                Keluar
              </button>
            </div>
          </div>
        ) : (
          <button
            type="button"
            onClick={() => {
              onNavigate?.();
              navigate("/login");
            }}
            className="flex w-full items-center justify-center gap-2 rounded-xl bg-amber-500 px-3 py-2.5 text-xs font-extrabold text-slate-950 hover:bg-amber-400"
          >
            <LogIn className="h-4 w-4" />
            Masuk / Daftar Akun
          </button>
        )}
      </div>
    </div>
  );

  return (
    <div className="flex min-h-screen bg-slate-50">
      {/* Sidebar Desktop / Laptop */}
      <aside className="hidden w-72 shrink-0 lg:sticky lg:top-0 lg:block lg:h-screen">
        {renderSidebarContent()}
      </aside>

      {/* Drawer Mobile / Android */}
      {mobileOpen && (
        <div className="fixed inset-0 z-50 flex lg:hidden">
          <div
            className="fixed inset-0 bg-slate-950/60"
            onClick={() => setMobileOpen(false)}
          />
          <div className="relative z-10 h-full w-72 max-w-[85vw] shadow-2xl">
            <button
              type="button"
              onClick={() => setMobileOpen(false)}
              aria-label="Tutup menu samping"
              className="absolute right-3 top-3 rounded-lg bg-white/10 p-1.5 text-white"
            >
              <X className="h-5 w-5" />
            </button>
            {renderSidebarContent(() => setMobileOpen(false))}
          </div>
        </div>
      )}

      {/* Konten Utama */}
      <div className="flex min-w-0 flex-1 flex-col">
        <header className="sticky top-0 z-30 border-b border-slate-200 bg-white/95 px-4 py-3 backdrop-blur sm:px-6">
          <div className="flex flex-wrap items-center justify-between gap-3">
            <div className="flex items-center gap-3">
              <button
                type="button"
                onClick={() => setMobileOpen(true)}
                aria-label="Buka sidebar navigasi"
                className="inline-flex h-10 w-10 items-center justify-center rounded-xl border border-slate-200 text-slate-700 hover:bg-slate-100 lg:hidden"
              >
                <Menu className="h-5 w-5" />
              </button>
              <div>
                <h1 className="text-lg font-extrabold text-slate-900 sm:text-xl">
                  {pageTitle}
                </h1>
                {pageSubtitle && (
                  <p className="text-xs text-slate-500">{pageSubtitle}</p>
                )}
              </div>
            </div>

            <div className="flex items-center gap-2">
              {currentUser && (
                <div className="hidden items-center gap-2 rounded-full border border-slate-200 bg-slate-50 px-3 py-1 sm:inline-flex">
                  <ShieldCheck className="h-4 w-4 text-emerald-600" />
                  <span className="text-xs font-semibold text-slate-700">
                    {currentUser.fullName}
                  </span>
                  <RoleBadge role={currentUser.role} />
                </div>
              )}
              {headerAction}
            </div>
          </div>

          {feedback && (
            <div
              className={`mt-3 flex items-center justify-between gap-3 rounded-xl border px-3.5 py-2 text-xs font-semibold ${
                feedback.type === "success"
                  ? "border-emerald-200 bg-emerald-50 text-emerald-800"
                  : feedback.type === "error"
                    ? "border-rose-200 bg-rose-50 text-rose-800"
                    : "border-blue-200 bg-blue-50 text-blue-800"
              }`}
            >
              <span>{feedback.text}</span>
              <button
                type="button"
                onClick={clearFeedback}
                aria-label="Tutup notifikasi"
                className="rounded p-0.5 hover:bg-black/5"
              >
                <X className="h-3.5 w-3.5" />
              </button>
            </div>
          )}
        </header>

        <main className="mx-auto w-full max-w-6xl flex-1 p-4 sm:p-6">
          {children}
        </main>
      </div>
    </div>
  );
};
