import React, { createContext, useCallback, useContext, useMemo, useState } from "react";
import { getFirebaseStatus, type FirebaseRuntimeStatus } from "../config/firebase";
import { GOOGLE_DRIVE_CONFIG } from "../config/googleDrive";
import type { SiadsDemoSnapshot } from "../services/demoData";
import {
  listFilesInTargetDriveFolder,
  requestGoogleDriveAccessToken,
  uploadFileToSchoolDriveFolder,
  type DriveUploadedFile
} from "../services/googleDriveService";
import {
  siadsService,
  type ActionResult,
  type CreateDocumentPayload,
  type RegisterPayload,
  type VerifyTargetPayload
} from "../services/siadsService";
import type { UserProfile, UserRole } from "../types/siads";

export interface FeedbackBanner {
  type: "success" | "error" | "info";
  text: string;
}

interface SiadsContextValue {
  snapshot: SiadsDemoSnapshot;
  currentUser: UserProfile | null;
  firebaseStatus: FirebaseRuntimeStatus;
  feedback: FeedbackBanner | null;
  driveAccessToken: string | null;
  driveFiles: DriveUploadedFile[];
  isDriveBusy: boolean;
  clearFeedback: () => void;
  setFeedback: (banner: FeedbackBanner) => void;
  loginDemoByRole: (role: UserRole) => ActionResult<UserProfile>;
  loginByEmail: (email: string) => ActionResult<UserProfile>;
  registerSelfService: (payload: RegisterPayload) => ActionResult<UserProfile>;
  logout: () => void;
  addDocument: (
    payload: Omit<CreateDocumentPayload, "actor">
  ) => Promise<ActionResult>;
  verifyTarget: (payload: Omit<VerifyTargetPayload, "reviewer">) => ActionResult;
  approveAndUpdateUserRole: (
    targetUid: string,
    newRole: UserRole,
    approve: boolean
  ) => ActionResult;
  connectGoogleDrive: () => Promise<boolean>;
  uploadToGoogleDrive: (
    file: File,
    description?: string
  ) => Promise<DriveUploadedFile | null>;
  refreshDriveFiles: () => Promise<void>;
}

const SiadsContext = createContext<SiadsContextValue | null>(null);

export const SiadsProvider: React.FC<{ children: React.ReactNode }> = ({
  children
}) => {
  const [snapshot, setSnapshot] = useState<SiadsDemoSnapshot>(() =>
    siadsService.getSnapshot()
  );
  const [currentUser, setCurrentUser] = useState<UserProfile | null>(null);
  const [feedback, setFeedback] = useState<FeedbackBanner | null>(null);
  const [driveAccessToken, setDriveAccessToken] = useState<string | null>(null);
  const [driveFiles, setDriveFiles] = useState<DriveUploadedFile[]>([]);
  const [isDriveBusy, setIsDriveBusy] = useState(false);

  const firebaseStatus = useMemo(() => getFirebaseStatus(), []);

  const refreshSnapshot = useCallback(() => {
    setSnapshot(siadsService.getSnapshot());
  }, []);

  const clearFeedback = useCallback(() => setFeedback(null), []);

  const loginDemoByRole = useCallback(
    (role: UserRole) => {
      const res = siadsService.loginDemoByRole(role);
      if (res.ok && res.data) {
        setCurrentUser(res.data);
        setFeedback({ type: "success", text: res.message });
      } else {
        setFeedback({ type: "error", text: res.message });
      }
      refreshSnapshot();
      return res;
    },
    [refreshSnapshot]
  );

  const loginByEmail = useCallback(
    (email: string) => {
      const res = siadsService.loginByEmail(email);
      if (res.ok && res.data) {
        setCurrentUser(res.data);
        setFeedback({ type: "success", text: res.message });
      } else {
        setFeedback({ type: "error", text: res.message });
      }
      refreshSnapshot();
      return res;
    },
    [refreshSnapshot]
  );

  const registerSelfService = useCallback(
    (payload: RegisterPayload) => {
      const res = siadsService.registerSelfService(payload);
      setFeedback({
        type: res.ok ? "success" : "error",
        text: res.message
      });
      refreshSnapshot();
      return res;
    },
    [refreshSnapshot]
  );

  const logout = useCallback(() => {
    setCurrentUser(null);
    setFeedback({ type: "info", text: "Anda telah keluar dari sesi internal SIADS." });
  }, []);

  const addDocument = useCallback(
    async (payload: Omit<CreateDocumentPayload, "actor">) => {
      if (!currentUser) {
        const err: ActionResult = {
          ok: false,
          message: "Silakan login terlebih dahulu sebelum mengunggah dokumen."
        };
        setFeedback({ type: "error", text: err.message });
        return err;
      }
      const res = await siadsService.addDocument({
        ...payload,
        actor: currentUser
      });
      setFeedback({
        type: res.ok ? "success" : "error",
        text: res.message
      });
      refreshSnapshot();
      return res;
    },
    [currentUser, refreshSnapshot]
  );

  const verifyTarget = useCallback(
    (payload: Omit<VerifyTargetPayload, "reviewer">) => {
      if (!currentUser) {
        const err: ActionResult = {
          ok: false,
          message: "Silakan login terlebih dahulu."
        };
        setFeedback({ type: "error", text: err.message });
        return err;
      }
      const res = siadsService.verifyTarget({
        ...payload,
        reviewer: currentUser
      });
      setFeedback({
        type: res.ok ? "success" : "error",
        text: res.message
      });
      refreshSnapshot();
      return res;
    },
    [currentUser, refreshSnapshot]
  );

  const approveAndUpdateUserRole = useCallback(
    (targetUid: string, newRole: UserRole, approve: boolean) => {
      if (!currentUser) {
        const err: ActionResult = {
          ok: false,
          message: "Silakan login sebagai ADMIN terlebih dahulu."
        };
        setFeedback({ type: "error", text: err.message });
        return err;
      }
      const res = siadsService.approveAndUpdateUserRole(
        currentUser,
        targetUid,
        newRole,
        approve
      );
      setFeedback({
        type: res.ok ? "success" : "error",
        text: res.message
      });
      refreshSnapshot();
      return res;
    },
    [currentUser, refreshSnapshot]
  );

  const refreshDriveFiles = useCallback(async () => {
    if (!driveAccessToken) return;
    setIsDriveBusy(true);
    try {
      const files = await listFilesInTargetDriveFolder(driveAccessToken);
      setDriveFiles(files);
    } catch (err) {
      setFeedback({
        type: "error",
        text: err instanceof Error ? err.message : "Gagal memuat daftar Google Drive."
      });
    } finally {
      setIsDriveBusy(false);
    }
  }, [driveAccessToken]);

  const connectGoogleDrive = useCallback(async () => {
    setIsDriveBusy(true);
    try {
      const token = await requestGoogleDriveAccessToken();
      setDriveAccessToken(token);
      const files = await listFilesInTargetDriveFolder(token);
      setDriveFiles(files);
      setFeedback({
        type: "success",
        text: `Google Drive terhubung ke folder ${GOOGLE_DRIVE_CONFIG.targetFolderId}.`
      });
      return true;
    } catch (err) {
      setFeedback({
        type: "error",
        text: err instanceof Error ? err.message : "Gagal menghubungkan Google Drive."
      });
      return false;
    } finally {
      setIsDriveBusy(false);
    }
  }, []);

  const uploadToGoogleDrive = useCallback(
    async (file: File, description?: string) => {
      setIsDriveBusy(true);
      try {
        const token = driveAccessToken || (await requestGoogleDriveAccessToken());
        if (!driveAccessToken) {
          setDriveAccessToken(token);
        }
        const uploaded = await uploadFileToSchoolDriveFolder(
          file,
          token,
          description
        );
        const files = await listFilesInTargetDriveFolder(token);
        setDriveFiles(files);
        setFeedback({
          type: "success",
          text: `Berkas "${uploaded.name}" berhasil disimpan ke folder Google Drive SDN 1 Ngantru.`
        });
        return uploaded;
      } catch (err) {
        setFeedback({
          type: "error",
          text: err instanceof Error ? err.message : "Gagal mengunggah ke Google Drive."
        });
        return null;
      } finally {
        setIsDriveBusy(false);
      }
    },
    [driveAccessToken]
  );

  const value: SiadsContextValue = {
    snapshot,
    currentUser,
    firebaseStatus,
    feedback,
    driveAccessToken,
    driveFiles,
    isDriveBusy,
    clearFeedback,
    setFeedback,
    loginDemoByRole,
    loginByEmail,
    registerSelfService,
    logout,
    addDocument,
    verifyTarget,
    approveAndUpdateUserRole,
    connectGoogleDrive,
    uploadToGoogleDrive,
    refreshDriveFiles
  };

  return <SiadsContext.Provider value={value}>{children}</SiadsContext.Provider>;
};

export function useSiadsStore(): SiadsContextValue {
  const ctx = useContext(SiadsContext);
  if (!ctx) {
    throw new Error("useSiadsStore harus digunakan di dalam SiadsProvider.");
  }
  return ctx;
}
