import React from "react";
import type { UserRole, VerificationStatus } from "../types/siads";

const ROLE_STYLES: Record<UserRole, string> = {
  ADMIN: "bg-rose-100 text-rose-800 border-rose-200",
  KEPALA_SEKOLAH: "bg-indigo-100 text-indigo-800 border-indigo-200",
  GURU: "bg-emerald-100 text-emerald-800 border-emerald-200",
  OPERATOR: "bg-amber-100 text-amber-800 border-amber-200"
};

const ROLE_LABELS: Record<UserRole, string> = {
  ADMIN: "ADMIN",
  KEPALA_SEKOLAH: "KEPALA SEKOLAH",
  GURU: "GURU",
  OPERATOR: "OPERATOR"
};

export const RoleBadge: React.FC<{ role: UserRole }> = ({ role }) => (
  <span
    className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-bold tracking-wide ${ROLE_STYLES[role]}`}
  >
    {ROLE_LABELS[role]}
  </span>
);

const STATUS_STYLES: Record<VerificationStatus, string> = {
  DISETUJUI: "bg-emerald-100 text-emerald-800 border-emerald-200",
  MENUNGGU: "bg-amber-100 text-amber-800 border-amber-200",
  PERLU_REVISI: "bg-rose-100 text-rose-800 border-rose-200"
};

const STATUS_LABELS: Record<VerificationStatus, string> = {
  DISETUJUI: "Disetujui",
  MENUNGGU: "Menunggu",
  PERLU_REVISI: "Perlu Revisi"
};

export const StatusBadge: React.FC<{ status: VerificationStatus }> = ({ status }) => (
  <span
    className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold ${STATUS_STYLES[status]}`}
  >
    {STATUS_LABELS[status]}
  </span>
);
