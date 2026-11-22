import type { WebAppManifest } from "../types/webAppManifest";
import { CATEGORIES_META } from "../types/siads";
import { SCHOOL_DOMAIN_CONFIG } from "./domain";

export const PWA_THEME_COLORS = {
  themeColor: "#102A43",
  backgroundColor: "#F8FAFC"
} as const;

export const SCHOOL_PWA_MANIFEST: WebAppManifest = {
  name: "SIADS SD Negeri 1 Ngantru",
  short_name: "SIADS SDN1",
  description: "Sistem Informasi Akreditasi & Dokumentasi Sekolah SD Negeri 1 Ngantru",
  start_url: "/",
  scope: "/",
  display: "standalone",
  orientation: "any",
  lang: "id-ID",
  theme_color: PWA_THEME_COLORS.themeColor,
  background_color: PWA_THEME_COLORS.backgroundColor,
  categories: ["education", "productivity"],
  icons: [
    {
      src: SCHOOL_DOMAIN_CONFIG.logoPath,
      sizes: "192x192",
      type: "image/png",
      purpose: "any"
    },
    {
      src: SCHOOL_DOMAIN_CONFIG.logoPath,
      sizes: "512x512",
      type: "image/png",
      purpose: "any maskable"
    }
  ],
  shortcuts: CATEGORIES_META.map((cat) => ({
    name: cat.title,
    short_name: cat.shortTitle,
    description: cat.subtitle,
    url: `/kategori/${cat.id}`
  }))
};
