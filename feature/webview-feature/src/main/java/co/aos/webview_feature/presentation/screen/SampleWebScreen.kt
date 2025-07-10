package co.aos.webview_feature.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import co.aos.webview.BaseWebView
import co.aos.webview_feature.presentation.compose.LifecycleEventListener
import co.aos.webview_feature.presentation.viewmodel.WebViewModel

/**
 * 샘플 용 웹뷰 화면
 * */
@Composable
fun SampleWebScreen(
    modifier: Modifier = Modifier,
    viewModel: WebViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val baseWebView = remember { BaseWebView(context) }
    val uiState by viewModel.uiState.collectAsState()

    AndroidView(
        modifier = modifier,
        factory = {
            baseWebView.apply {
                loadWebViewUrl(uiState.webViewConfig.url)
            }
        }
    )

    // life cycle 이벤트 처리
    LifecycleEventListener(
        onResume = {
            baseWebView.onResume()
        },
        onPause = {
            baseWebView.onPause()
        },
        onDestroy = {
            baseWebView.destroy()
        }
    )

    // 뒤로가기 핸들링
    BackHandler{
        if (baseWebView.canGoBack()) {
            baseWebView.goBack()
        }
    }
}