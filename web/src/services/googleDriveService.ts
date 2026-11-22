import { GOOGLE_DRIVE_CONFIG } from "../config/googleDrive";

export interface DriveUploadedFile {
  id: string;
  name: string;
  mimeType: string;
  webViewLink: string;
  parents?: string[];
  modifiedTime?: string;
}

declare global {
  interface Window {
    google?: {
      accounts?: {
        oauth2?: {
          initTokenClient: (config: {
            client_id: string;
            scope: string;
            callback: (response: { access_token?: string; error?: string }) => void;
          }) => {
            requestAccessToken: (options?: { prompt?: string }) => void;
          };
        };
      };
    };
  }
}

let gisScriptPromise: Promise<void> | null = null;

function ensureGoogleIdentityScript(): Promise<void> {
  if (window.google?.accounts?.oauth2) {
    return Promise.resolve();
  }
  if (!gisScriptPromise) {
    gisScriptPromise = new Promise((resolve, reject) => {
      const existing = document.querySelector<HTMLScriptElement>(
        'script[src="https://accounts.google.com/gsi/client"]'
      );
      if (existing) {
        existing.addEventListener("load", () => resolve());
        existing.addEventListener("error", () =>
          reject(new Error("Gagal memuat Google Identity Services."))
        );
        return;
      }
      const script = document.createElement("script");
      script.src = "https://accounts.google.com/gsi/client";
      script.async = true;
      script.defer = true;
      script.onload = () => resolve();
      script.onerror = () =>
        reject(new Error("Gagal memuat skrip Google Identity Services."));
      document.head.appendChild(script);
    });
  }
  return gisScriptPromise;
}

export async function requestGoogleDriveAccessToken(): Promise<string> {
  const clientId = GOOGLE_DRIVE_CONFIG.oauthClientId.trim();
  if (!clientId) {
    throw new Error(
      "VITE_GOOGLE_DRIVE_CLIENT_ID belum diisi pada .env.local untuk autentikasi Google Drive Web."
    );
  }
  await ensureGoogleIdentityScript();
  const oauth2 = window.google?.accounts?.oauth2;
  if (!oauth2) {
    throw new Error("Google OAuth2 client tidak tersedia pada browser ini.");
  }

  return new Promise<string>((resolve, reject) => {
    const tokenClient = oauth2.initTokenClient({
      client_id: clientId,
      scope: GOOGLE_DRIVE_CONFIG.driveScope,
      callback: (response) => {
        if (response.error || !response.access_token) {
          reject(
            new Error(
              response.error || "Izin akses Google Drive dibatalkan atau gagal diperoleh."
            )
          );
          return;
        }
        resolve(response.access_token);
      }
    });
    tokenClient.requestAccessToken({ prompt: "consent" });
  });
}

export async function uploadFileToSchoolDriveFolder(
  file: File,
  accessToken: string,
  description?: string
): Promise<DriveUploadedFile> {
  const metadata = {
    name: file.name,
    parents: [GOOGLE_DRIVE_CONFIG.targetFolderId],
    description:
      description?.trim() || "Unggahan dokumen SIADS SD Negeri 1 Ngantru"
  };

  const form = new FormData();
  form.append(
    "metadata",
    new Blob([JSON.stringify(metadata)], { type: "application/json" })
  );
  form.append("file", file);

  const response = await fetch(GOOGLE_DRIVE_CONFIG.uploadEndpoint, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${accessToken}`
    },
    body: form
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`Gagal mengunggah ke Google Drive (${response.status}): ${errorText}`);
  }

  const data = (await response.json()) as Partial<DriveUploadedFile>;
  if (!data.id) {
    throw new Error("Respons Google Drive tidak menyertakan ID file.");
  }
  return {
    id: data.id,
    name: data.name || file.name,
    mimeType: data.mimeType || file.type || "application/octet-stream",
    webViewLink:
      data.webViewLink || `https://drive.google.com/file/d/${data.id}/view`,
    parents: data.parents || [GOOGLE_DRIVE_CONFIG.targetFolderId]
  };
}

export async function listFilesInTargetDriveFolder(
  accessToken: string
): Promise<DriveUploadedFile[]> {
  const query = encodeURIComponent(
    `'${GOOGLE_DRIVE_CONFIG.targetFolderId}' in parents and trashed = false`
  );
  const url = `${GOOGLE_DRIVE_CONFIG.filesEndpoint}?q=${query}&fields=files(id,name,mimeType,webViewLink,modifiedTime)&orderBy=modifiedTime desc&pageSize=20&supportsAllDrives=true&includeItemsFromAllDrives=true`;

  const response = await fetch(url, {
    headers: {
      Authorization: `Bearer ${accessToken}`
    }
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`Gagal membaca isi folder Drive (${response.status}): ${errorText}`);
  }

  const payload = (await response.json()) as { files?: DriveUploadedFile[] };
  return payload.files || [];
}
