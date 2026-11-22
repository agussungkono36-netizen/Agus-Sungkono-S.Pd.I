package com.example.siads.navigation

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.siads.config.SchoolDomainConfig
import com.example.siads.hooks.SiadsUiState
import com.example.siads.hooks.SiadsViewModel
import com.example.siads.pages.ActivityLogsPage
import com.example.siads.pages.CategoryPage
import com.example.siads.pages.DashboardPage
import com.example.siads.pages.DocumentsEvidencePage
import com.example.siads.pages.HomePage
import com.example.siads.pages.LoginPage
import com.example.siads.pages.UserManagementPage
import com.example.siads.pages.VerificationsPage
import com.example.siads.types.CategoryType
import com.example.siads.types.SiadsRoutes

/**
 * Konfigurasi NavHost terpusat untuk aplikasi SIADS SD Negeri 1 Ngantru.
 * Terhubung dengan Android App Links domain https://sdnegeri1ngantru.schoolpro.id.
 */
@Composable
fun SiadsNavHost(
    uiState: SiadsUiState,
    viewModel: SiadsViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = SiadsRoutes.HOME,
    pendingDeepLinkUri: Uri? = null,
    onDeepLinkHandled: () -> Unit = {}
) {
    val navigateToRoute: (String) -> Unit = { route ->
        if (route == SiadsRoutes.HOME) {
            navController.navigate(SiadsRoutes.HOME) {
                popUpTo(SiadsRoutes.HOME) { inclusive = false }
                launchSingleTop = true
            }
        } else {
            navController.navigate(route) {
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(pendingDeepLinkUri) {
        val targetRoute = SchoolDomainConfig.resolveRouteFromUri(pendingDeepLinkUri)
        if (targetRoute != null) {
            navigateToRoute(targetRoute)
            onDeepLinkHandled()
        }
    }

    val handleLogoutAndGoHome: () -> Unit = {
        viewModel.logout()
        navController.navigate(SiadsRoutes.HOME) {
            popUpTo(SiadsRoutes.HOME) { inclusive = true }
            launchSingleTop = true
        }
    }

    val navigateAfterAuthToDashboard: () -> Unit = {
        navController.navigate(SiadsRoutes.DASHBOARD) {
            popUpTo(SiadsRoutes.LOGIN) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(220)) },
        exitTransition = { fadeOut(animationSpec = tween(220)) },
        popEnterTransition = { fadeIn(animationSpec = tween(220)) },
        popExitTransition = { fadeOut(animationSpec = tween(220)) }
    ) {
        // 1. Homepage (Beranda Sekolah)
        composable(
            route = SiadsRoutes.HOME,
            deepLinks = listOf(
                navDeepLink { uriPattern = "${SchoolDomainConfig.BASE_URL}/" },
                navDeepLink { uriPattern = SchoolDomainConfig.BASE_URL }
            )
        ) {
            HomePage(
                uiState = uiState,
                onCategoryClick = { category ->
                    navigateToRoute(SiadsRoutes.categoryRoute(category.id))
                },
                onLoginClick = {
                    viewModel.clearFeedback()
                    navigateToRoute(SiadsRoutes.LOGIN)
                },
                onDashboardClick = {
                    navigateToRoute(SiadsRoutes.DASHBOARD)
                }
            )
        }

        // 2. Halaman Kategori (5 Kategori Utama berdasarkan categoryId)
        composable(
            route = SiadsRoutes.CATEGORY_PATTERN,
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                    defaultValue = CategoryType.PENGEMBANGAN_DIRI.id
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "${SchoolDomainConfig.BASE_URL}/kategori/{categoryId}" },
                navDeepLink { uriPattern = "${SchoolDomainConfig.BASE_URL}/category/{categoryId}" }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            val category = CategoryType.fromId(categoryId)
            CategoryPage(
                category = category,
                uiState = uiState,
                onNavigate = navigateToRoute,
                onBackHome = {
                    navController.popBackStack(SiadsRoutes.HOME, inclusive = false)
                },
                onLogout = handleLogoutAndGoHome,
                buildDriveAuthRequest = { viewModel.buildDriveAuthRequest() },
                onDriveTokenGranted = { token -> viewModel.onDriveAuthorized(token) },
                onDriveAuthError = { msg -> viewModel.onDriveAuthorizationError(msg) },
                onUploadDocument = { title, catId, docNumber, fileUri ->
                    viewModel.uploadAndSubmitDocument(title, catId, docNumber, fileUri)
                }
            )
        }

        // 3. Halaman Login & Pendaftaran Mandiri
        composable(
            route = SiadsRoutes.LOGIN,
            deepLinks = listOf(
                navDeepLink { uriPattern = SchoolDomainConfig.URL_LOGIN },
                navDeepLink { uriPattern = "${SchoolDomainConfig.BASE_URL}/masuk" }
            )
        ) {
            LoginPage(
                uiState = uiState,
                onLoginWithEmail = { email, password ->
                    if (viewModel.loginWithEmail(email, password)) {
                        navigateAfterAuthToDashboard()
                    }
                },
                onLoginDemoRole = { role ->
                    if (viewModel.loginAsDemoRole(role)) {
                        navigateAfterAuthToDashboard()
                    }
                },
                onRegisterUser = { name, email, nip, role ->
                    viewModel.registerUser(name, email, nip, role)
                },
                onBackHome = {
                    navController.popBackStack(SiadsRoutes.HOME, inclusive = false)
                }
            )
        }

        // 4. Halaman Dashboard Berdasarkan Peran Pengguna
        composable(
            route = SiadsRoutes.DASHBOARD,
            deepLinks = listOf(
                navDeepLink { uriPattern = SchoolDomainConfig.URL_DASHBOARD }
            )
        ) {
            DashboardPage(
                uiState = uiState,
                onNavigate = navigateToRoute,
                onLogout = handleLogoutAndGoHome,
                onReviewDocument = { docId, status, notes ->
                    viewModel.reviewDocument(docId, status, notes)
                },
                onApproveUser = { uid ->
                    viewModel.approveUser(uid)
                },
                buildDriveAuthRequest = { viewModel.buildDriveAuthRequest() },
                onDriveTokenGranted = { token -> viewModel.onDriveAuthorized(token) },
                onDriveAuthError = { msg -> viewModel.onDriveAuthorizationError(msg) }
            )
        }

        // 5. Halaman Internal Tambahan (Sidebar Navigation)
        composable(
            route = SiadsRoutes.DOCUMENTS,
            deepLinks = listOf(
                navDeepLink { uriPattern = SchoolDomainConfig.URL_DOCUMENTS }
            )
        ) {
            DocumentsEvidencePage(
                uiState = uiState,
                onNavigate = navigateToRoute,
                onLogout = handleLogoutAndGoHome,
                onSelectFilter = { category ->
                    viewModel.setCategoryFilter(category)
                },
                buildDriveAuthRequest = { viewModel.buildDriveAuthRequest() },
                onDriveTokenGranted = { token -> viewModel.onDriveAuthorized(token) },
                onDriveAuthError = { msg -> viewModel.onDriveAuthorizationError(msg) },
                onUploadDocument = { title, catId, docNumber, fileUri ->
                    viewModel.uploadAndSubmitDocument(title, catId, docNumber, fileUri)
                }
            )
        }

        composable(
            route = SiadsRoutes.VERIFICATIONS,
            deepLinks = listOf(
                navDeepLink { uriPattern = SchoolDomainConfig.URL_VERIFICATIONS }
            )
        ) {
            VerificationsPage(
                uiState = uiState,
                onNavigate = navigateToRoute,
                onLogout = handleLogoutAndGoHome,
                onReviewDocument = { docId, status, notes ->
                    viewModel.reviewDocument(docId, status, notes)
                }
            )
        }

        composable(route = SiadsRoutes.ACTIVITY_LOGS) {
            ActivityLogsPage(
                uiState = uiState,
                onNavigate = navigateToRoute,
                onLogout = handleLogoutAndGoHome
            )
        }

        composable(route = SiadsRoutes.USER_MANAGEMENT) {
            UserManagementPage(
                uiState = uiState,
                onNavigate = navigateToRoute,
                onLogout = handleLogoutAndGoHome,
                onApproveUser = { uid ->
                    viewModel.approveUser(uid)
                },
                onChangeUserRole = { uid, newRole ->
                    viewModel.changeUserRole(uid, newRole)
                }
            )
        }
    }
}
