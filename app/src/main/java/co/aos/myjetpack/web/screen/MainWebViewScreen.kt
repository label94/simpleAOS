package co.aos.myjetpack.web.screen

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import co.aos.base.utils.moveActivity
import co.aos.myjetpack.setting.SettingActivity
import co.aos.myjetpack.sub.SubWebActivity
import co.aos.myutils.log.LogUtil
import co.aos.network_error_feature.screen.NetworkErrorScreen
import co.aos.network_error_feature.state.NetworkStatusContract
import co.aos.network_error_feature.viewmodel.NetworkStatusViewModel
import co.aos.webview.BaseWebView
import co.aos.webview_renewal_feature.consts.WebConstants
import co.aos.webview_renewal_feature.js.JsBridge
import co.aos.webview_renewal_feature.state.WebViewContract
import co.aos.webview_renewal_feature.view.RefreshSwipeWebView
import co.aos.webview_renewal_feature.view.WebViewBackHandler
import co.aos.webview_renewal_feature.view.WebViewLifeCycle
import co.aos.webview_renewal_feature.viewmodel.WebViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 메인 웹뷰 화면
 * */
@Composable
fun MainWebViewScreen(
    networkStatusViewModel: NetworkStatusViewModel,
    webViewModel: WebViewModel
) {
    // snack bar 상태
    val snackBarHostState = remember { SnackbarHostState() }

    // context 관련
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val activity = LocalActivity.current

    // 코루틴 상태
    val coroutineScope = rememberCoroutineScope()

    // ui 관련 상태
    var isLoading by remember { mutableStateOf(false) }
    val webUiState by webViewModel.uiState.collectAsState()
    val networkUiState by networkStatusViewModel.uiState.collectAsState()

    // 파일 선택 결과 전달
    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        LogUtil.i(LogUtil.WEB_VIEW_LOG_TAG, "fileLauncher code : ${result.resultCode} \n data : ${result.data}")

        // 선택한 파일을 웹으로 보내는 이벤트 실행
        webViewModel.setEvent(WebViewContract.Event.FileChooserResult(
            resultCode = result.resultCode,
            intent = result.data
        ))
    }

    // 카메라 권한 요청 및 처리
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isPermission ->
        LogUtil.i(LogUtil.WEB_VIEW_LOG_TAG, "cameraPermissionLauncher : $isPermission")

        // 카메라 권한 허용 여부에 따른 이벤트 실행
        webViewModel.setEvent(WebViewContract.Event.ReOpenFileChooser(isPermission))
    }

    // 웹뷰
    val baseWebView = remember {
        BaseWebView(context).apply {
            // 웹과 상호작용 하기 위한 브릿지 추가
            addWebViewJavascriptInterface(JsBridge(
                callback = { jsEvent ->
                    // 자바스크립트 인터페이스 이벤트 처리
                    webViewModel.onJsEvent(jsEvent)
                }
            ), WebConstants.JS_BRIDGE_NAME)

            // 웹뷰 내 페이지 전환 시 호출
            shouldOverrideUrlLoadingCallback = { url ->
                false
            }

            // 웹뷰에서 파일 탐색기 호출
            onShowFileChooserCallback = { filePathCallback, fileChooserParams ->
                webViewModel.setEvent(WebViewContract.Event.ShowFileChooser(filePathCallback, fileChooserParams))
                true
            }
        }
    }

    // 공통 Effect 처리
    EffectHandler(
        webViewModel = webViewModel,
        networkStatusViewModel = networkStatusViewModel,
        baseWebView = baseWebView,
        activity = activity,
        fileLauncher = fileLauncher,
        cameraPermissionLauncher = cameraPermissionLauncher
    )

    // 생명 주기 관련 처리
    WebViewLifeCycle(
        baseWebView = baseWebView,
        lifecycleOwner = lifecycleOwner
    )

    // 뒤로가기 처리
    WebViewBackHandler(
        baseWebView = baseWebView,
        webViewModel = webViewModel
    )

    // UI 영역
    WebViewScreen(
        baseWebView = baseWebView,
        webUiState = webUiState,
        networkUiState = networkUiState,
        snackBarHostState = snackBarHostState,
        isLoading = isLoading,
        onSwipeRefresh = {
            // pull to refresh 시 이벤트 실행
            isLoading = true
            webViewModel.setEvent(WebViewContract.Event.WebViewReLoad)

            coroutineScope.launch {
                delay(1000)
                isLoading = false
            }
        },
        onRetry = {
            // 웹뷰 리로드
            networkStatusViewModel.setEvent(NetworkStatusContract.Event.ReloadWebView)
        }
    )
}

/**
 * 웹뷰가 포함 된 컴포즈 UI 화면
 * */
@Composable
private fun WebViewScreen(
    baseWebView: BaseWebView,
    webUiState: WebViewContract.State,
    networkUiState: NetworkStatusContract.State,
    snackBarHostState: SnackbarHostState,
    isLoading: Boolean,
    onSwipeRefresh: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            // 웹뷰 추가
            RefreshSwipeWebView(
                baseWebView = baseWebView,
                isLoading = isLoading,
                onRefresh = {
                    // pull to refresh 시 이벤트 실행
                    onSwipeRefresh.invoke()
                },
                enableSwipe = webUiState.isSwipeEnable
            )
        }
    }

    // 네트워크 연결 상태 오류 UI 표시
    if (networkUiState.isShowErrorScreen) {
        NetworkErrorScreen(
            onRetry = {
                // 웹뷰 리로드
                onRetry.invoke()
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
    activity: Activity?,
    fileLauncher: ActivityResultLauncher<Intent>,
    cameraPermissionLauncher: ActivityResultLauncher<String>
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

        // 3.웹뷰 이펙트 감지
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
                    is WebViewContract.Effect.LaunchFileChooser -> {
                        fileLauncher.launch(effect.intent)
                    }
                    is WebViewContract.Effect.RequestCameraPermission -> {
                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                    is WebViewContract.Effect.SubWebViewOpen -> {
                        activity?.moveActivity(SubWebActivity::class.java, WebConstants.MOVE_URL, effect.url)
                    }
                    is WebViewContract.Effect.OpenAppSetting -> {
                        activity?.moveActivity(SettingActivity::class.java)
                    }
                }
            }
        }
    }
}