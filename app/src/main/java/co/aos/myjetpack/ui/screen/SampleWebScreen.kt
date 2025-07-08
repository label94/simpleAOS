package co.aos.myjetpack.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import co.aos.myjetpack.compose.LifecycleEventListener
import co.aos.webview.BaseWebView

/**
 * 샘플 용 웹뷰 화면
 * */
@Composable
fun SampleWebScreen(
    modifier: Modifier = Modifier,
    url: String,
) {
    val context = LocalContext.current
    val baseWebView = remember { BaseWebView(context) }

    AndroidView(
        modifier = modifier,
        factory = {
            baseWebView.apply {
                loadWebViewUrl(url)
            }
        },
        update = { webView ->
            // url 변경 시 갱신
            webView.loadWebViewUrl(url)
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