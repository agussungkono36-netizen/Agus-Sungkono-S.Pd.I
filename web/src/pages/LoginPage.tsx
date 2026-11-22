import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft, KeyRound, ShieldAlert, UserPlus } from "lucide-react";
import { SCHOOL_DOMAIN_CONFIG } from "../config/domain";
import { useSiadsStore } from "../hooks/useSiadsStore";
import { InternalSidebarLayout } from "../layouts/InternalSidebarLayout";
import type { UserRole } from "../types/siads";

const DEMO_ROLES: {
  role: UserRole;
  title: string;
  desc: string;
}[] = [
  {
    role: "ADMIN",
    title: "Masuk sebagai ADMIN",
    desc: "Kelola pengguna, setujui akun baru, kelola kategori & skema."
  },
  {
    role: "KEPALA_SEKOLAH",
    title: "Masuk sebagai KEPALA SEKOLAH",
    desc: "Verifikasi dokumen akreditasi, setujui/revisi bukti fisik."
  },
  {
    role: "GURU",
    title: "Masuk sebagai GURU",
    desc: "Unggah modul ajar, bukti kegiatan & dokumen kategori."
  },
  {
    role: "OPERATOR",
    title: "Masuk sebagai OPERATOR",
    desc: "Kelola administrasi RKAS, inventaris & arsip GTK."
  }
];

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const {
    firebaseStatus,
    loginDemoByRole,
    loginByEmail,
    registerSelfService
  } = useSiadsStore();

  const [tab, setTab] = useState<"login" | "register">("login");
  const [email, setEmail] = useState("");
  const [fullName, setFullName] = useState("");
  const [regEmail, setRegEmail] = useState("");
  const [nipOrNuptk, setNipOrNuptk] = useState("");
  const [requestedRole, setRequestedRole] = useState<
    Exclude<UserRole, "ADMIN">
  >("GURU");

  const handleQuickDemo = (role: UserRole) => {
    const res = loginDemoByRole(role);
    if (res.ok) {
      navigate("/dashboard");
    }
  };

  const handleEmailLogin = (e: React.FormEvent) => {
    e.preventDefault();
    const res = loginByEmail(email);
    if (res.ok) {
      navigate("/dashboard");
    }
  };

  const handleRegister = (e: React.FormEvent) => {
    e.preventDefault();
    const res = registerSelfService({
      fullName,
      email: regEmail,
      nipOrNuptk,
      requestedRole
    });
    if (res.ok) {
      setFullName("");
      setRegEmail("");
      setNipOrNuptk("");
      setTab("login");
    }
  };

  return (
    <InternalSidebarLayout
      pageTitle="Portal Autentikasi SIADS"
      pageSubtitle={`${SCHOOL_DOMAIN_CONFIG.schoolName} · ${SCHOOL_DOMAIN_CONFIG.primaryDomain}`}
      headerAction={
        <button
          type="button"
          onClick={() => navigate("/")}
          className="inline-flex items-center gap-1.5 rounded-xl border border-slate-300 bg-white px-3 py-2 text-xs font-bold text-slate-700 hover:bg-slate-50"
        >
          <ArrowLeft className="h-4 w-4" />
          Beranda
        </button>
      }
    >
      <div className="grid gap-6 lg:grid-cols-2">
        {/* Form Login / Register */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <div className="flex gap-2 rounded-xl bg-slate-100 p-1">
            <button
              type="button"
              onClick={() => setTab("login")}
              className={`flex-1 rounded-lg py-2 text-xs font-extrabold transition ${
                tab === "login"
                  ? "bg-white text-navy shadow-sm"
                  : "text-slate-600 hover:text-slate-900"
              }`}
            >
              Masuk Akun
            </button>
            <button
              type="button"
              onClick={() => setTab("register")}
              className={`flex-1 rounded-lg py-2 text-xs font-extrabold transition ${
                tab === "register"
                  ? "bg-white text-navy shadow-sm"
                  : "text-slate-600 hover:text-slate-900"
              }`}
            >
              Daftar Akun Baru
            </button>
          </div>

          {tab === "login" ? (
            <form onSubmit={handleEmailLogin} className="mt-5 space-y-4">
              <div>
                <h2 className="text-lg font-extrabold text-slate-900">
                  Masuk dengan Email Sekolah
                </h2>
                <p className="mt-1 text-xs text-slate-600">
                  Status Backend: {firebaseStatus.statusMessage}
                </p>
              </div>

              <div>
                <label className="mb-1 block text-xs font-bold text-slate-700">
                  Email Terdaftar
                </label>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="contoh: guru.demo@sdnegeri1ngantru.schoolpro.id"
                  required
                  className="w-full rounded-xl border border-slate-300 px-3.5 py-2.5 text-sm focus:border-navy focus:outline-none"
                />
              </div>

              <button
                type="submit"
                className="flex w-full items-center justify-center gap-2 rounded-xl bg-navy px-4 py-3 text-sm font-extrabold text-white hover:bg-slate-800"
              >
                <KeyRound className="h-4 w-4" />
                Masuk ke Dashboard
              </button>
            </form>
          ) : (
            <form onSubmit={handleRegister} className="mt-5 space-y-4">
              <div className="rounded-xl border border-amber-200 bg-amber-50 p-3 text-xs text-amber-900">
                <div className="flex items-start gap-2">
                  <ShieldAlert className="mt-0.5 h-4 w-4 shrink-0 text-amber-700" />
                  <p>
                    <strong>Kebijakan Keamanan:</strong> Pendaftaran mandiri tidak
                    pernah memberikan hak akses <strong>ADMIN</strong>. Akun baru harus
                    disetujui Admin sekolah terlebih dahulu.
                  </p>
                </div>
              </div>

              <div>
                <label className="mb-1 block text-xs font-bold text-slate-700">
                  Nama Lengkap & Gelar
                </label>
                <input
                  type="text"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  required
                  placeholder="Contoh: Siti Nurhaliza, S.Pd"
                  className="w-full rounded-xl border border-slate-300 px-3.5 py-2 text-sm focus:border-navy focus:outline-none"
                />
              </div>

              <div>
                <label className="mb-1 block text-xs font-bold text-slate-700">
                  Email Aktif
                </label>
                <input
                  type="email"
                  value={regEmail}
                  onChange={(e) => setRegEmail(e.target.value)}
                  required
                  placeholder="nama@sdnegeri1ngantru.schoolpro.id"
                  className="w-full rounded-xl border border-slate-300 px-3.5 py-2 text-sm focus:border-navy focus:outline-none"
                />
              </div>

              <div className="grid gap-3 sm:grid-cols-2">
                <div>
                  <label className="mb-1 block text-xs font-bold text-slate-700">
                    NIP / NUPTK
                  </label>
                  <input
                    type="text"
                    value={nipOrNuptk}
                    onChange={(e) => setNipOrNuptk(e.target.value)}
                    required
                    placeholder="18 digit NIP/NUPTK"
                    className="w-full rounded-xl border border-slate-300 px-3.5 py-2 text-sm focus:border-navy focus:outline-none"
                  />
                </div>
                <div>
                  <label className="mb-1 block text-xs font-bold text-slate-700">
                    Peran Diajukan (Non-Admin)
                  </label>
                  <select
                    value={requestedRole}
                    onChange={(e) =>
                      setRequestedRole(
                        e.target.value as Exclude<UserRole, "ADMIN">
                      )
                    }
                    className="w-full rounded-xl border border-slate-300 px-3.5 py-2 text-sm focus:border-navy focus:outline-none"
                  >
                    <option value="GURU">GURU</option>
                    <option value="OPERATOR">OPERATOR</option>
                    <option value="KEPALA_SEKOLAH">KEPALA SEKOLAH</option>
                  </select>
                </div>
              </div>

              <button
                type="submit"
                className="flex w-full items-center justify-center gap-2 rounded-xl bg-emerald-600 px-4 py-3 text-sm font-extrabold text-white hover:bg-emerald-700"
              >
                <UserPlus className="h-4 w-4" />
                Kirim Pendaftaran (Menunggu Admin)
              </button>
            </form>
          )}
        </div>

        {/* Uji Cepat 4 Peran Demo Terisolasi */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <div className="flex items-center justify-between gap-2">
            <h2 className="text-lg font-extrabold text-slate-900">
              Preview Uji 4 Peran Demo
            </h2>
            <span className="rounded-full bg-amber-100 px-2.5 py-1 text-[11px] font-bold text-amber-900">
              Data Demo Terisolasi
            </span>
          </div>
          <p className="mt-1 text-xs text-slate-600">
            Gunakan akun simulasi di bawah untuk menguji alur Dashboard berdasarkan
            peran tanpa menyentuh database produksi.
          </p>

          <div className="mt-4 space-y-3">
            {DEMO_ROLES.map((item) => (
              <button
                key={item.role}
                type="button"
                data-testid={`demo-login-${item.role.toLowerCase()}`}
                onClick={() => handleQuickDemo(item.role)}
                className="w-full rounded-2xl border border-slate-200 bg-slate-50 p-4 text-left transition hover:border-navy hover:bg-white hover:shadow-md"
              >
                <div className="flex items-center justify-between">
                  <span className="text-sm font-extrabold text-navy">
                    {item.title}
                  </span>
                  <span className="rounded-lg bg-navy px-2.5 py-1 text-[11px] font-bold text-white">
                    Masuk →
                  </span>
                </div>
                <p className="mt-1 text-xs text-slate-600">{item.desc}</p>
              </button>
            ))}
          </div>
        </div>
      </div>
    </InternalSidebarLayout>
  );
};
