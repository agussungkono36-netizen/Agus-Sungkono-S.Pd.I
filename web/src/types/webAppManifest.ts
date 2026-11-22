export interface WebAppIcon {
  src: string;
  sizes: string;
  type: string;
  purpose?: "any" | "maskable" | "monochrome" | "any maskable";
}

export interface WebAppShortcut {
  name: string;
  short_name?: string;
  description?: string;
  url: string;
  icons?: WebAppIcon[];
}

export interface WebAppManifest {
  name: string;
  short_name: string;
  description: string;
  start_url: string;
  scope: string;
  display: "standalone" | "minimal-ui" | "fullscreen" | "browser";
  orientation: "any" | "natural" | "portrait" | "landscape";
  lang: string;
  theme_color: string;
  background_color: string;
  categories?: string[];
  icons: WebAppIcon[];
  shortcuts?: WebAppShortcut[];
}
