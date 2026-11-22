import { initializeApp, getApps, type FirebaseApp } from "firebase/app";
import { getFirestore, type Firestore } from "firebase/firestore";
import { getAuth, type Auth } from "firebase/auth";

export const FIRESTORE_COLLECTIONS = {
  USERS: "users",
  TEACHERS: "teachers",
  CATEGORIES: "categories",
  DOCUMENTS: "documents",
  ACCREDITATION_STANDARDS: "accreditationStandards",
  EVIDENCE: "evidence",
  VERIFICATIONS: "verifications",
  ACTIVITY_LOGS: "activityLogs"
} as const;

export interface FirebaseRuntimeStatus {
  isConfigured: boolean;
  mode: "DEMO_PREVIEW" | "PRODUCTION_FIREBASE";
  projectId: string | null;
  statusMessage: string;
}

const envConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY as string | undefined,
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN as string | undefined,
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID as string | undefined,
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET as string | undefined,
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID as string | undefined,
  appId: import.meta.env.VITE_FIREBASE_APP_ID as string | undefined,
  measurementId: import.meta.env.VITE_FIREBASE_MEASUREMENT_ID as string | undefined
};

export function isFirebaseEnvConfigured(): boolean {
  return Boolean(
    envConfig.apiKey &&
      envConfig.authDomain &&
      envConfig.projectId &&
      envConfig.appId
  );
}

export function getFirebaseStatus(): FirebaseRuntimeStatus {
  const configured = isFirebaseEnvConfigured();
  return {
    isConfigured: configured,
    mode: configured ? "PRODUCTION_FIREBASE" : "DEMO_PREVIEW",
    projectId: configured ? envConfig.projectId ?? null : null,
    statusMessage: configured
      ? `Terhubung ke Firebase (${envConfig.projectId})`
      : "Mode Preview Demo — variabel lingkungan Firebase (.env.local) belum dikonfigurasi."
  };
}

let cachedApp: FirebaseApp | null = null;
let cachedDb: Firestore | null = null;
let cachedAuth: Auth | null = null;

export function getFirebaseServices(): {
  app: FirebaseApp | null;
  db: Firestore | null;
  auth: Auth | null;
} {
  if (!isFirebaseEnvConfigured()) {
    return { app: null, db: null, auth: null };
  }
  if (!cachedApp) {
    cachedApp = getApps().length ? getApps()[0] : initializeApp(envConfig);
    cachedDb = getFirestore(cachedApp);
    cachedAuth = getAuth(cachedApp);
  }
  return { app: cachedApp, db: cachedDb, auth: cachedAuth };
}
