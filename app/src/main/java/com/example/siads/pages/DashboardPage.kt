package com.example.siads.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.siads.components.DemoModeBanner
import com.example.siads.components.DocumentItemCard
import com.example.siads.components.GoogleDriveStorageCard
import com.example.siads.components.RoleBadge
import com.example.siads.components.StatSummaryCard
import com.example.siads.components.rememberDriveAuthLauncher
import com.example.siads.config.FirestoreCollections
import com.example.siads.hooks.SiadsUiState
import com.example.siads.layouts.InternalSidebarLayout
import com.example.siads.types.CategoryType
import com.example.siads.types.SiadsRoutes
import com.example.siads.types.UserProfile
import com.example.siads.types.UserRole
import com.example.siads.types.VerificationStatus
import com.example.ui.theme.CategoryLingkungan
import com.example.ui.theme.CategoryPembelajaran
import com.example.ui.theme.CategoryProfilSekolah
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusVerified
import com.google.android.gms.auth.api.identity.AuthorizationRequest

@Composable
fun DashboardPage(
    uiState: SiadsUiState,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onReviewDocument: (docId: String, status: VerificationStatus, notes: String) -> Unit,
    onApproveUser: (uid: String) -> Unit,
    buildDriveAuthRequest: () -> AuthorizationRequest,
    onDriveTokenGranted: (String) -> Unit,
    onDriveAuthError: (String) -> Unit
) {
    val user = uiState.currentUser
    val launchDriveAuth = rememberDriveAuthLauncher(
        buildAuthRequest = buildDriveAuthRequest,
        onTokenGranted = onDriveTokenGranted,
        onAuthError = onDriveAuthError
    )
    InternalSidebarLayout(
        title = user?.let { "Dashboard ${it.role.label}" } ?: "Dashboard Internal",
        currentRoute = SiadsRoutes.DASHBOARD,
        currentUser = user,
        onNavigate = onNavigate,
        onLogout = onLogout
    ) { isExpanded ->
        if (user == null) {
            UnauthenticatedDashboardFallback(onGoLogin = { onNavigate(SiadsRoutes.LOGIN) })
            return@InternalSidebarLayout
        }

        val docs = uiState.snapshot.documents
        val pendingDocs = docs.filter { it.status == VerificationStatus.MENUNGGU }
        val approvedDocs = docs.filter { it.status == VerificationStatus.DISETUJUI }
        val pendingUsers = uiState.snapshot.users.filter { !it.isApprovedByAdmin }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier
                        .widthIn(max = 960.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DemoModeBanner(firebaseStatus = uiState.firebaseStatus)

                    GoogleDriveStorageCard(
                        isDriveConnected = uiState.isDriveConnected,
                        driveFileCount = uiState.driveFiles.size,
                        onConnectDrive = launchDriveAuth
                    )

                    // Kartu Sambutan Peran
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RoleBadge(role = user.role)
                                Text(
                                    text = "NIP/NUPTK: ${user.nipOrNuptk}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Selamat datang, ${user.fullName}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = user.role.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                            )
                        }
                    }

                    // Statistik ringkas responsif (2 kolom)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatSummaryCard(
                            title = "Total Dokumen",
                            value = "${docs.size}",
                            subtitle = "${approvedDocs.size} disetujui",
                            accentColor = MaterialTheme.colorScheme.primary,
                            icon = Icons.Filled.FolderCopy,
                            modifier = Modifier.weight(1f)
                        )
                        StatSummaryCard(
                            title = "Antrean Verifikasi",
                            value = "${pendingDocs.size}",
                            subtitle = "Menunggu tinjauan",
                            accentColor = StatusPending,
                            icon = Icons.Filled.PendingActions,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatSummaryCard(
                            title = "Data Guru (GTK)",
                            value = "${uiState.snapshot.teachers.size}",
                            subtitle = "Aktif di SDN 1 Ngantru",
                            accentColor = CategoryLingkungan,
                            icon = Icons.Filled.Groups,
                            modifier = Modifier.weight(1f)
                        )
                        StatSummaryCard(
                            title = if (user.role == UserRole.ADMIN) "Koleksi Firestore" else "Bukti Fisik",
                            value = if (user.role == UserRole.ADMIN) {
                                "${FirestoreCollections.allCollections.size}"
                            } else {
                                "${uiState.snapshot.evidence.size}"
                            },
                            subtitle = if (user.role == UserRole.ADMIN) "Siap integrasi" else "Evidence akreditasi",
                            accentColor = CategoryPembelajaran,
                            icon = if (user.role == UserRole.ADMIN) Icons.Filled.Storage else Icons.Filled.Verified,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Aksi Cepat Berdasarkan Peran
                    RoleSpecificQuickActions(
                        user = user,
                        isExpanded = isExpanded,
                        onNavigate = onNavigate
                    )
                }
            }

            // Bagian Khusus Peran
            when (user.role) {
                UserRole.ADMIN -> {
                    item {
                        SectionHeaderRow(
                            title = "Persetujuan Pendaftar Baru (${pendingUsers.size})",
                            modifier = Modifier.widthIn(max = 960.dp)
                        )
                    }
                    if (pendingUsers.isEmpty()) {
                        item {
                            EmptyRoleTipCard(
                                text = "Tidak ada pendaftar mandiri yang menunggu persetujuan Admin saat ini.",
                                modifier = Modifier.widthIn(max = 960.dp)
                            )
                        }
                    } else {
                        items(pendingUsers, key = { it.uid }) { pending ->
                            Card(
                                modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(pending.fullName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text("${pending.email} • Peran: ${pending.role.badgeCode}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Button(
                                        onClick = { onApproveUser(pending.uid) },
                                        modifier = Modifier.testTag("approve_user_${pending.uid}")
                                    ) {
                                        Text("Setujui")
                                    }
                                }
                            }
                        }
                    }
                }

                UserRole.KEPALA_SEKOLAH -> {
                    item {
                        SectionHeaderRow(
                            title = "Antrean Verifikasi Kepala Sekolah (${pendingDocs.size})",
                            modifier = Modifier.widthIn(max = 960.dp)
                        )
                    }
                    if (pendingDocs.isEmpty()) {
                        item {
                            EmptyRoleTipCard(
                                text = "Seluruh dokumen akreditasi saat ini telah terverifikasi.",
                                modifier = Modifier.widthIn(max = 960.dp)
                            )
                        }
                    } else {
                        items(pendingDocs, key = { it.id }) { doc ->
                            DocumentItemCard(
                                document = doc,
                                modifier = Modifier.widthIn(max = 960.dp)
                            ) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            onReviewDocument(doc.id, VerificationStatus.DISETUJUI, "Disetujui Kepala Sekolah")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusVerified),
                                        modifier = Modifier.weight(1f).testTag("verify_approve_${doc.id}")
                                    ) {
                                        Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Setujui")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            onReviewDocument(doc.id, VerificationStatus.PERLU_REVISI, "Mohon lengkapi lampiran")
                                        },
                                        modifier = Modifier.weight(1f).testTag("verify_revise_${doc.id}")
                                    ) {
                                        Text("Minta Revisi")
                                    }
                                }
                            }
                        }
                    }
                }

                UserRole.GURU -> {
                    item {
                        SectionHeaderRow(
                            title = "Dokumen Pembelajaran & Pengembangan Diri",
                            modifier = Modifier.widthIn(max = 960.dp)
                        )
                    }
                    val guruDocs = docs.filter {
                        it.categoryId == CategoryType.PEMBELAJARAN.id ||
                            it.categoryId == CategoryType.PENGEMBANGAN_DIRI.id
                    }
                    items(guruDocs, key = { it.id }) { doc ->
                        DocumentItemCard(document = doc, modifier = Modifier.widthIn(max = 960.dp))
                    }
                }

                UserRole.OPERATOR -> {
                    item {
                        SectionHeaderRow(
                            title = "Berkas Tata Kelola, Profil & Lingkungan",
                            modifier = Modifier.widthIn(max = 960.dp)
                        )
                    }
                    val operatorDocs = docs.filter {
                        it.categoryId == CategoryType.TATA_KELOLA_ADMINISTRASI.id ||
                            it.categoryId == CategoryType.PROFIL_SEKOLAH.id ||
                            it.categoryId == CategoryType.LINGKUNGAN.id
                    }
                    items(operatorDocs, key = { it.id }) { doc ->
                        DocumentItemCard(document = doc, modifier = Modifier.widthIn(max = 960.dp))
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun RoleSpecificQuickActions(
    user: UserProfile,
    isExpanded: Boolean,
    onNavigate: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Pintasan Kerja ${user.role.label}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            val actions = when (user.role) {
                UserRole.ADMIN -> listOf(
                    Triple("Manajemen Pengguna", Icons.Filled.ManageAccounts, SiadsRoutes.USER_MANAGEMENT),
                    Triple("Log Aktivitas", Icons.Filled.History, SiadsRoutes.ACTIVITY_LOGS),
                    Triple("Semua Dokumen", Icons.Filled.FolderCopy, SiadsRoutes.DOCUMENTS)
                )
                UserRole.KEPALA_SEKOLAH -> listOf(
                    Triple("Verifikasi & Standar", Icons.Filled.Verified, SiadsRoutes.VERIFICATIONS),
                    Triple("Manajemen Pengguna", Icons.Filled.ManageAccounts, SiadsRoutes.USER_MANAGEMENT),
                    Triple("Kategori Pembelajaran", Icons.Filled.Description, SiadsRoutes.categoryRoute(CategoryType.PEMBELAJARAN.id))
                )
                UserRole.GURU -> listOf(
                    Triple("Unggah / Kelola Dokumen", Icons.Filled.AddCircleOutline, SiadsRoutes.DOCUMENTS),
                    Triple("Pengembangan Diri", Icons.Filled.Description, SiadsRoutes.categoryRoute(CategoryType.PENGEMBANGAN_DIRI.id)),
                    Triple("Kategori Pembelajaran", Icons.Filled.Description, SiadsRoutes.categoryRoute(CategoryType.PEMBELAJARAN.id))
                )
                UserRole.OPERATOR -> listOf(
                    Triple("Tata Kelola & Adm.", Icons.Filled.Description, SiadsRoutes.categoryRoute(CategoryType.TATA_KELOLA_ADMINISTRASI.id)),
                    Triple("Profil Sekolah", Icons.Filled.Description, SiadsRoutes.categoryRoute(CategoryType.PROFIL_SEKOLAH.id)),
                    Triple("Unggah Berkas", Icons.Filled.AddCircleOutline, SiadsRoutes.DOCUMENTS)
                )
            }

            if (isExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    actions.forEach { (label, icon, route) ->
                        OutlinedButton(
                            onClick = { onNavigate(route) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(label, maxLines = 1)
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    actions.forEach { (label, icon, route) ->
                        OutlinedButton(
                            onClick = { onNavigate(route) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeaderRow(title: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun EmptyRoleTipCard(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun UnauthenticatedDashboardFallback(onGoLogin: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sesi Internal Belum Aktif",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Silakan masuk melalui halaman Login atau pilih salah satu Akun Demo Preview.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onGoLogin, modifier = Modifier.height(48.dp)) {
            Text("Buka Halaman Login")
        }
    }
}
