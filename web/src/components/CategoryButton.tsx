import React from "react";
import {
  Award,
  BookOpen,
  Briefcase,
  ChevronRight,
  Landmark,
  Leaf
} from "lucide-react";
import type { CategoryId, CategoryMeta } from "../types/siads";

const ICON_MAP: Record<CategoryId, React.ComponentType<{ className?: string }>> = {
  pengembangan_diri: Award,
  lingkungan: Leaf,
  pembelajaran: BookOpen,
  tata_kelola_administrasi: Briefcase,
  profil_sekolah: Landmark
};

interface CategoryButtonProps {
  category: CategoryMeta;
  documentCount: number;
  onClick: () => void;
}

export const CategoryButton: React.FC<CategoryButtonProps> = ({
  category,
  documentCount,
  onClick
}) => {
  const Icon = ICON_MAP[category.id];
  return (
    <button
      type="button"
      onClick={onClick}
      data-testid={`category-btn-${category.id}`}
      className={`group flex w-full items-center justify-between gap-4 rounded-2xl px-5 py-4 text-left shadow-md transition-all duration-150 hover:-translate-y-0.5 hover:shadow-xl focus:outline-none focus:ring-4 focus:ring-slate-300 ${category.bgClass} ${category.textClass}`}
    >
      <div className="flex items-start gap-4">
        <div className="mt-0.5 flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-black/15">
          <Icon className="h-6 w-6" />
        </div>
        <div>
          <div className="flex flex-wrap items-center gap-2">
            <span className="text-base font-extrabold tracking-wide sm:text-lg">
              {category.title}
            </span>
            <span className="rounded-full bg-black/20 px-2.5 py-0.5 text-[11px] font-bold">
              {documentCount} Dokumen
            </span>
          </div>
          <p className="mt-1 text-xs opacity-90 sm:text-sm">{category.subtitle}</p>
        </div>
      </div>
      <ChevronRight className="h-6 w-6 shrink-0 opacity-85 transition-transform group-hover:translate-x-1" />
    </button>
  );
};
