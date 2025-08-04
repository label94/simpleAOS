package co.aos.webview_renewal_feature.js

import android.webkit.JavascriptInterface
import co.aos.myutils.log.LogUtil

/**
 * 웹과 통신을 위한 자바스크립트 인터페이스 브릿지
 * */
class JsBridge(
    private val callback: (JsEvent) -> Unit
) {
    /** 화면 닫기 */
    @JavascriptInterface
    fun close() {
        LogUtil.d(LogUtil.JS_LOG_TAG, "close() call!")
        callback(JsEvent.OnJsClose)
    }

    /** 서브 웹뷰 열기 */
    @JavascriptInterface
    fun openSubWebView(url: String?) {
        LogUtil.d(LogUtil.JS_LOG_TAG, "openSubWebView() call! url : $url")

        if (url.isNullOrEmpty()) return
        callback(JsEvent.SubWebViewOpen(url))
    }

    /** 외부 브라우저 열기 */
    @JavascriptInterface
    fun openExternalBrowser(url: String?) {
        LogUtil.d(LogUtil.JS_LOG_TAG, "openExternalBrowser() call! url : $url")

        if (url.isNullOrEmpty()) return
        callback(JsEvent.OpenWebBrowser(url))
    }
}