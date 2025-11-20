package co.aos.webview_renewal_feature.view

import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import co.aos.myutils.log.LogUtil
import co.aos.webview.BaseWebView
import co.aos.webview_renewal_feature.state.WebViewContract
import co.aos.webview_renewal_feature.viewmodel.WebViewModel

/**
 * 커스텀 웹뷰
 * - 스와이프 기능이 있는 웹뷰
 *
 * @param baseWebView 웹뷰
 * @param isLoading 스와이프 리프레시 되었는지 유무
 * @param onRefresh 스와이프 리프레시 되었을 때 작업 수행
 * @param enableSwipe pull to refresh 기능 활성화 유무
 * */
@Composable
fun RefreshSwipeWebView(
    baseWebView: BaseWebView,
    isLoading: Boolean,
    onRefresh:() -> Unit,
    enableSwipe: Boolean
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { con ->
            // 최초 1회만 실행
            SwipeRefreshLayout(con).apply {
                // 웹 css height 100% 먹지 않는 현상 해결을 위해 viewGroup 내 속성을 MATCH_PARENT로 설정
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                isEnabled = enableSwipe // pull to refresh 비활성화/활성화 설정 유무

                setOnRefreshListener {
                    // 스와이프 되었을 때 작업 수행
                    onRefresh.invoke()
                }
                addView(
                    baseWebView,
                    // 웹 css height 100% 먹지 않는 현상 해결을 위해 viewGroup 내 속성을 MATCH_PARENT로 설정
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }
        },
        update = { swipeRefreshLayout ->
            // 상태 변경 시 마다 호출
            swipeRefreshLayout.isRefreshing = isLoading
            swipeRefreshLayout.isEnabled = enableSwipe
        }
    )
}

/**
 * Android lifecycle 에 따른 웹뷰 처리
 * */
@Composable
fun WebViewLifeCycle(
    baseWebView: BaseWebView,
    lifecycleOwner: LifecycleOwner
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_RESUME -> {
                    baseWebView.onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    baseWebView.onPause()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    baseWebView.destroy()
                }
                else -> Unit
            }
        }

        // lifecycle 등록
        lifecycleOwner.lifecycle.addObserver(observer)

        // 메모리 해제
        onDispose {
            LogUtil.i(LogUtil.WEB_VIEW_LOG_TAG, "WebViewLifeCycle onDispose()")

            // 등록 해제 및 웹뷰 destroy
            lifecycleOwner.lifecycle.removeObserver(observer)
            baseWebView.destroy()
        }
    }
}

/**
 * 웹뷰 뒤로가기 핸들러
 * */
@Composable
fun WebViewBackHandler(
    baseWebView: BaseWebView,
    webViewModel: WebViewModel
) {
    BackHandler {
        webViewModel.setEvent(WebViewContract.Event.OnBackPress(
            isWebViewCanGoBack = baseWebView.canGoBack(),
            currentWebViewUrl = baseWebView.url
        ))
    }
}