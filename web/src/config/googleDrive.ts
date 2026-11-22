export const GOOGLE_DRIVE_CONFIG = {
  targetFolderId: "1rPq3eEgEZbO_uvFSEO7qEwWm4XFWoRVl",
  targetFolderUrl:
    "https://drive.google.com/drive/folders/1rPq3eEgEZbO_uvFSEO7qEwWm4XFWoRVl?usp=sharing",
  driveScope: "https://www.googleapis.com/auth/drive",
  uploadEndpoint:
    "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart&fields=id,name,mimeType,webViewLink,parents&supportsAllDrives=true",
  filesEndpoint: "https://www.googleapis.com/drive/v3/files",
  oauthClientId: (import.meta.env.VITE_GOOGLE_DRIVE_CLIENT_ID as string | undefined) || ""
} as const;
