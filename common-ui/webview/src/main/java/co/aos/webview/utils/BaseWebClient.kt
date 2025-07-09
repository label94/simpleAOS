package co.aos.webview.utils

import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView

/**
 * WebView Client 관련 인터페이스
 * */
interface BaseWebClient {
    /** 웹뷰 페이지 로드 할 때 호출 */
    fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean

    /** 웹뷰 페이지 로드 시작이 될 때 호출 */
    fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?)

    /** 웹뷰 페이지 로드 완료가 될 때 호출 */
    fun onPageFinished(view: WebView?, url: String?)

    /** 웹뷰 내 에러 발생 시 호출 */
    fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    )
}