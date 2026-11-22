package com.example.siads.layouts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.siads.components.RoleBadge
import com.example.siads.components.categoryIcon
import com.example.siads.config.SchoolDomainConfig
import com.example.siads.hooks.rememberResponsiveLayoutInfo
import com.example.siads.types.CategoryType
import com.example.siads.types.SiadsRoutes
import com.example.siads.types.UserProfile
import com.example.siads.types.UserRole
import kotlinx.coroutines.launch

data class SidebarNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val testTag: String,
    val accentTint: Color? = null
)

/**
 * Layout sidebar responsif untuk halaman internal SIADS:
 * - Tablet / Laptop (lebar >= 840dp): PermanentNavigationDrawer di sisi kiri
 * - Android Phone (lebar < 840dp): ModalNavigationDrawer dengan tombol menu di TopAppBar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternalSidebarLayout(
    title: String,
    currentRoute: String,
    currentUser: UserProfile?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    content: @Composable (isExpandedScreen: Boolean) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val layoutInfo = rememberResponsiveLayoutInfo(maxWidth)

        val mainNavItems = buildList {
            add(SidebarNavItem(SiadsRoutes.HOME, "Beranda Sekolah", Icons.Filled.Home, "nav_home"))
            if (currentUser != null) {
                add(SidebarNavItem(SiadsRoutes.DASHBOARD, "Dashboard Peran", Icons.Filled.Dashboard, "nav_dashboard"))
            } else {
                add(SidebarNavItem(SiadsRoutes.LOGIN, "Login Portal Internal", Icons.AutoMirrored.Filled.Login, "nav_login"))
            }
            add(SidebarNavItem(SiadsRoutes.DOCUMENTS, "Dokumen & Bukti", Icons.Filled.FolderCopy, "nav_documents"))
            add(SidebarNavItem(SiadsRoutes.VERIFICATIONS, "Verifikasi & Standar", Icons.Filled.Verified, "nav_verifications"))
            add(SidebarNavItem(SiadsRoutes.ACTIVITY_LOGS, "Log Aktivitas", Icons.Filled.History, "nav_logs"))
            if (currentUser?.role == UserRole.ADMIN || currentUser?.role == UserRole.KEPALA_SEKOLAH) {
                add(SidebarNavItem(SiadsRoutes.USER_MANAGEMENT, "Manajemen Pengguna", Icons.Filled.ManageAccounts, "nav_users"))
            }
        }

        val drawerContent: @Composable () -> Unit = {
            SidebarDrawerContent(
                currentRoute = currentRoute,
                currentUser = currentUser,
                mainItems = mainNavItems,
                onSelectRoute = { route ->
                    scope.launch { drawerState.close() }
                    onNavigate(route)
                },
                onLogout = {
                    scope.launch { drawerState.close() }
                    onLogout()
                }
            )
        }

        if (layoutInfo.showPermanentSidebar) {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        modifier = Modifier.width(288.dp),
                        drawerContainerColor = MaterialTheme.colorScheme.surface
                    ) {
                        drawerContent()
                    }
                }
            ) {
                InternalScaffoldShell(
                    title = title,
                    showMenuButton = false,
                    onMenuClick = {},
                    currentUser = currentUser,
                    onNavigateLogin = { onNavigate(SiadsRoutes.LOGIN) }
                ) {
                    content(true)
                }
            }
        } else {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier.width(292.dp),
                        drawerContainerColor = MaterialTheme.colorScheme.surface
                    ) {
                        drawerContent()
                    }
                }
            ) {
                InternalScaffoldShell(
                    title = title,
                    showMenuButton = true,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    currentUser = currentUser,
                    onNavigateLogin = { onNavigate(SiadsRoutes.LOGIN) }
                ) {
                    content(false)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InternalScaffoldShell(
    title: String,
    showMenuButton: Boolean,
    onMenuClick: () -> Unit,
    currentUser: UserProfile?,
    onNavigateLogin: () -> Unit,
    body: @Composable () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "SIADS SD Negeri 1 Ngantru",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    if (showMenuButton) {
                        IconButton(
                            onClick = onMenuClick,
                            modifier = Modifier.testTag("sidebar_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = stringResource(id = R.string.cd_open_menu)
                            )
                        }
                    }
                },
                actions = {
                    if (currentUser != null) {
                        RoleBadge(
                            role = currentUser.role,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    } else {
                        Surface(
                            onClick = onNavigateLogin,
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .testTag("topbar_login_button")
                        ) {
                            Text(
                                text = "Masuk",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            body()
        }
    }
}

@Composable
private fun SidebarDrawerContent(
    currentRoute: String,
    currentUser: UserProfile?,
    mainItems: List<SidebarNavItem>,
    onSelectRoute: (String) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp)
    ) {
        // Identitas Sekolah di Sidebar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_sdn1_ngantru_logo),
                contentDescription = stringResource(id = R.string.cd_school_logo),
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "SIADS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "SD Negeri 1 Ngantru",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = SchoolDomainConfig.PRIMARY_DOMAIN,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (currentUser != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = currentUser.fullName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentUser.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    RoleBadge(role = currentUser.role)
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp, horizontal = 14.dp))

        Text(
            text = "MENU INTERNAL",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
        )

        mainItems.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label, style = MaterialTheme.typography.labelLarge) },
                icon = { Icon(item.icon, contentDescription = null) },
                selected = currentRoute == item.route,
                onClick = { onSelectRoute(item.route) },
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .testTag(item.testTag)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 14.dp))

        Text(
            text = "5 KATEGORI AKREDITASI",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
        )

        CategoryType.entries.forEach { category ->
            val targetRoute = SiadsRoutes.categoryRoute(category.id)
            NavigationDrawerItem(
                label = {
                    Text(
                        text = category.shortTitle,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(category.accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryIcon(category),
                            contentDescription = null,
                            tint = category.onAccentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                selected = currentRoute == targetRoute,
                onClick = { onSelectRoute(targetRoute) },
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .testTag("sidebar_cat_${category.id}")
            )
        }

        if (currentUser != null) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 14.dp))
            NavigationDrawerItem(
                label = {
                    Text(
                        "Keluar Sesi",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                selected = false,
                onClick = onLogout,
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .testTag("sidebar_logout_button")
            )
        }
    }
}
