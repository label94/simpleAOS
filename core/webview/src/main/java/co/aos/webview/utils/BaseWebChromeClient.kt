package co.aos.webview.utils

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient.FileChooserParams
import android.webkit.WebView

/**
 * Chrome Client 관련 인터페이스
 * */
interface BaseWebChromeClient {

    /** 웹뷰 내 파일 탐색기 열기 */
    fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<out Uri?>?>?,
        fileChooserParams: FileChooserParams?
    ): Boolean
}