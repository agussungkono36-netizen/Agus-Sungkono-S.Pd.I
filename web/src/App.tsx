import React from "react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { SiadsProvider } from "./hooks/useSiadsStore";
import { CategoryPage } from "./pages/CategoryPage";
import { DashboardPage } from "./pages/DashboardPage";
import { HomePage } from "./pages/HomePage";
import {
  ActivityLogsPage,
  DocumentsPage,
  UserManagementPage,
  VerificationsPage
} from "./pages/InternalPages";
import { LoginPage } from "./pages/LoginPage";

export const App: React.FC = () => {
  return (
    <SiadsProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/kategori/:categoryId" element={<CategoryPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/dokumen" element={<DocumentsPage />} />
          <Route path="/verifikasi" element={<VerificationsPage />} />
          <Route path="/log-aktivitas" element={<ActivityLogsPage />} />
          <Route path="/pengguna" element={<UserManagementPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </SiadsProvider>
  );
};

export default App;
