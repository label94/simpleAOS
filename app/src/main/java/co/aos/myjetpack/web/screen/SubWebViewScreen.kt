package co.aos.myjetpack.web.screen

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import co.aos.network_error_feature.screen.NetworkErrorScreen
import co.aos.network_error_feature.state.NetworkStatusContract
import co.aos.network_error_feature.viewmodel.NetworkStatusViewModel
import co.aos.webview.BaseWebView
import co.aos.webview_renewal_feature.state.WebViewContract
import co.aos.webview_renewal_feature.view.WebViewBackHandler
import co.aos.webview_renewal_feature.view.WebViewLifeCycle
import co.aos.webview_renewal_feature.viewmodel.WebViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 서브 웹뷰 컴포즈 UI
 * */
@Composable
fun SubWebViewScreen(
    webViewModel: WebViewModel,
    networkStatusViewModel: NetworkStatusViewModel
) {
    // context 관련
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val activity = LocalActivity.current
    val coroutineScope = rememberCoroutineScope()

    // 네트워크 연결 관련 상태
    val networkUiState by networkStatusViewModel.uiState.collectAsState()

    // 웹뷰
    val baseWebView = remember {
        BaseWebView(context)
    }

    // 공통 effect 처리
    EffectHandler(
        webViewModel = webViewModel,
        networkStatusViewModel = networkStatusViewModel,
        baseWebView = baseWebView,
        activity = activity
    )

    // 생명 주기 처리
    WebViewLifeCycle(
        baseWebView = baseWebView,
        lifecycleOwner = lifecycleOwner
    )

    // 웹뷰 UI 화면
    WebViewScreen(
        baseWebView = baseWebView,
        networkUiState = networkUiState,
        onRetry = {
            // 웹뷰 리로드
            networkStatusViewModel.setEvent(NetworkStatusContract.Event.ReloadWebView)
        }
    )

    // 뒤로가기 처리
    WebViewBackHandler(
        baseWebView = baseWebView,
        webViewModel = webViewModel
    )
}

@Composable
private fun WebViewScreen(
    baseWebView: BaseWebView,
    networkUiState: NetworkStatusContract.State,
    onRetry: (() -> Unit)? = null
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            // 웹뷰 추가
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { con ->
                    baseWebView
                }
            )
        }
    }

    // 네트워크 연결 상태 오류 UI 표시
    if (networkUiState.isShowErrorScreen) {
        NetworkErrorScreen(
            onRetry = {
                onRetry?.invoke()
            }
        )
    }
}

/**
 * Launched Effect 처리를 위한 공통 Handler
 * */
@Composable
private fun EffectHandler(
    webViewModel: WebViewModel,
    networkStatusViewModel: NetworkStatusViewModel,
    baseWebView: BaseWebView,
    activity: Activity?
) {
    LaunchedEffect(Unit) {
        // 1. URL 변경 감지 -> 웹뷰 로드
        launch {
            webViewModel.uiState
                .map { it.loadUrl }
                .distinctUntilChanged()
                .collect { url ->
                    baseWebView.loadWebViewUrl(url)
                }
        }

        // 2. 네트워크 이펙트 감지
        launch {
            networkStatusViewModel.effect.collect { effect ->
                when(effect) {
                    is NetworkStatusContract.Effect.Retry -> {
                        webViewModel.setEvent(WebViewContract.Event.WebViewReLoad)
                    }
                }
            }
        }

        // 3. 웹뷰 이펙트 감지
        launch {
            webViewModel.effect.collect { effect ->
                when(effect) {
                    is WebViewContract.Effect.WebViewReload -> {
                        baseWebView.reload()
                    }
                    is WebViewContract.Effect.FinishActivity -> {
                        activity?.finish()
                    }
                    is WebViewContract.Effect.BackHistoryWebView -> {
                        baseWebView.goBack()
                    }
                    else -> {}
                }
            }
        }
    }
}