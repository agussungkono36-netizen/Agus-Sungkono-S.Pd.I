import React from "react";
import { SCHOOL_DOMAIN_CONFIG } from "../config/domain";

interface SchoolHeaderProps {
  compact?: boolean;
  onLoginClick?: () => void;
  loggedInLabel?: string;
}

export const SchoolHeader: React.FC<SchoolHeaderProps> = ({
  compact = false,
  onLoginClick,
  loggedInLabel
}) => {
  return (
    <header className="relative overflow-hidden rounded-3xl bg-navy text-white shadow-xl">
      <div
        className={`relative w-full ${compact ? "h-48 sm:h-56" : "h-64 sm:h-80 md:h-[360px]"}`}
      >
        <img
          src={SCHOOL_DOMAIN_CONFIG.headerPhotoPath}
          alt="Gedung Sekolah SD Negeri 1 Ngantru Berkilau"
          className="h-full w-full object-cover object-center"
        />
        <div className="absolute inset-0 bg-gradient-to-b from-slate-950/75 via-slate-900/55 to-navy/95" />

        {onLoginClick && (
          <div className="absolute right-4 top-4 z-20 flex items-center gap-2">
            <a
              href={SCHOOL_DOMAIN_CONFIG.baseUrl}
              target="_blank"
              rel="noreferrer"
              className="hidden rounded-full bg-white/15 px-3 py-1.5 text-xs font-medium text-white backdrop-blur transition hover:bg-white/25 sm:inline-flex"
            >
              {SCHOOL_DOMAIN_CONFIG.primaryDomain}
            </a>
            <button
              type="button"
              onClick={onLoginClick}
              data-testid="header-login-button"
              className="rounded-full bg-amber-500 px-4 py-2 text-xs font-bold text-slate-950 shadow-md transition hover:bg-amber-400"
            >
              {loggedInLabel || "Portal Login"}
            </button>
          </div>
        )}

        <div className="absolute inset-x-0 bottom-0 top-0 z-10 flex flex-col items-center justify-center px-4 text-center">
          <div className="mb-3 flex h-20 w-20 items-center justify-center rounded-full bg-white/95 p-1.5 shadow-lg ring-4 ring-amber-400/70 sm:h-24 sm:w-24">
            <img
              src={SCHOOL_DOMAIN_CONFIG.logoPath}
              alt="Logo Resmi SD Negeri 1 Ngantru"
              className="h-full w-full rounded-full object-contain"
            />
          </div>

          <span className="mb-1 rounded-full bg-amber-400/20 px-3 py-0.5 text-[11px] font-semibold uppercase tracking-widest text-amber-300 ring-1 ring-amber-300/40">
            SIADS · Sistem Informasi Akreditasi & Dokumentasi Sekolah
          </span>

          <h1 className="text-2xl font-extrabold tracking-wide text-white drop-shadow sm:text-4xl">
            {SCHOOL_DOMAIN_CONFIG.schoolName}
          </h1>
        </div>
      </div>

      <div className="border-t border-white/10 bg-navy/95 px-5 py-4 text-center sm:px-8">
        <p className="text-xs font-extrabold uppercase tracking-[0.22em] text-amber-300">
          VISI SD
        </p>
        <p className="mx-auto mt-1 max-w-3xl text-sm font-medium leading-relaxed text-slate-100 sm:text-base">
          “{SCHOOL_DOMAIN_CONFIG.visiSd}”
        </p>
      </div>
    </header>
  );
};
