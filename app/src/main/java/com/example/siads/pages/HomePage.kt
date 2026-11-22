package com.example.siads.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.siads.components.CategoryActionButton
import com.example.siads.components.DemoModeBanner
import com.example.siads.components.SchoolHeaderBanner
import com.example.siads.components.VisiSdCard
import com.example.siads.config.SchoolDomainConfig
import com.example.siads.hooks.SiadsUiState
import com.example.siads.hooks.rememberResponsiveLayoutInfo
import com.example.siads.types.CategoryType

@Composable
fun HomePage(
    uiState: SiadsUiState,
    onCategoryClick: (CategoryType) -> Unit,
    onLoginClick: () -> Unit,
    onDashboardClick: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val context = LocalContext.current
            val layoutInfo = rememberResponsiveLayoutInfo(maxWidth)
            val useTwoColumns = !layoutInfo.isCompact

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SchoolHeaderBanner(
                    topActionSlot = {
                        if (uiState.currentUser != null) {
                            Button(
                                onClick = onDashboardClick,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD97706),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("home_dashboard_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Dashboard,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Dashboard (${uiState.currentUser.role.badgeCode})",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Surface(
                                onClick = onLoginClick,
                                shape = RoundedCornerShape(50),
                                color = Color(0xEE102A43),
                                contentColor = Color.White,
                                shadowElevation = 4.dp,
                                modifier = Modifier.testTag("home_login_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Login,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Login Internal",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                )

                // Konten utama dengan lebar maksimal agar nyaman di laptop & tablet
                Column(
                    modifier = Modifier
                        .widthIn(max = 920.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    VisiSdCard()

                    Text(
                        text = "KATEGORI DOKUMENTASI & AKREDITASI",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    val categories = CategoryType.entries
                    if (useTwoColumns) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            categories.chunked(2).forEach { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    pair.forEach { category ->
                                        val docCount = uiState.snapshot.documents.count {
                                            it.categoryId == category.id
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            CategoryActionButton(
                                                category = category,
                                                documentCount = docCount,
                                                onClick = { onCategoryClick(category) }
                                            )
                                        }
                                    }
                                    if (pair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            categories.forEach { category ->
                                val docCount = uiState.snapshot.documents.count {
                                    it.categoryId == category.id
                                }
                                CategoryActionButton(
                                    category = category,
                                    documentCount = docCount,
                                    onClick = { onCategoryClick(category) }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = if (uiState.isAuthenticated) onDashboardClick else onLoginClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("home_portal_cta"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.isAuthenticated) Icons.Filled.Dashboard else Icons.AutoMirrored.Filled.Login,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.isAuthenticated) "Dashboard Internal" else "Portal Internal",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { SchoolDomainConfig.openWebPortal(context) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("home_web_domain_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Language,
                                contentDescription = "Buka portal web ${SchoolDomainConfig.PRIMARY_DOMAIN}",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Portal Web",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Domain Resmi: ${SchoolDomainConfig.PRIMARY_DOMAIN}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    DemoModeBanner(firebaseStatus = uiState.firebaseStatus)

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
