package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.siads.hooks.SiadsViewModel
import com.example.siads.navigation.SiadsNavHost
import com.example.ui.theme.SiadsTheme

class MainActivity : ComponentActivity() {

    private var pendingDeepLinkUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingDeepLinkUri = intent?.data
        enableEdgeToEdge()
        setContent {
            SiadsTheme {
                SiadsApp(
                    pendingDeepLinkUri = pendingDeepLinkUri,
                    onDeepLinkHandled = { pendingDeepLinkUri = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingDeepLinkUri = intent.data
    }
}

@Composable
fun SiadsApp(
    viewModel: SiadsViewModel = viewModel(),
    pendingDeepLinkUri: Uri? = null,
    onDeepLinkHandled: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SiadsNavHost(
        uiState = uiState,
        viewModel = viewModel,
        pendingDeepLinkUri = pendingDeepLinkUri,
        onDeepLinkHandled = onDeepLinkHandled
    )
}
