package co.aos.webview

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.fragment.app.Fragment
import co.aos.myutils.common.AppConstants
import co.aos.myutils.log.LogUtil
import co.aos.webview.utils.BaseWebChromeClient
import co.aos.webview.utils.BaseWebClient
import java.util.LinkedHashMap

/**
 * 웹뷰 상태 관리를 위한 웹뷰 프래그먼트
 *
 * - 메모리 안전
 * - MVI 구조에 적합
 * - WebView 히스토리, 세션, 스크롤 모두 자동 유지
 * */
class BaseWebViewFragment: Fragment(), BaseWebChromeClient, BaseWebClient {

    /** 웹뷰 */
    private lateinit var webView: BaseWebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 웹뷰 로드 설정
        webView = BaseWebView(requireContext()).apply {
            arguments?.getString(AppConstants.WEB_LOAD_UA_KEY)?.let {
                setCustomUserAgent(it)
            }
            arguments?.getString(AppConstants.WEB_LOAD_URL_KEY)?.let {
                loadWebViewUrl(it)
            }
        }
        LogUtil.i(LogUtil.WEB_VIEW_LOG_TAG, "webView Full UA : ${webView.settings.userAgentString}")

        // 웹뷰 관련 리스너 설정
        webView.setWebViewClientInterface(this)
        webView.setWebViewChromeClientInterface(this)

        return webView
    }

    /** 웹뷰 뒤로가기 */
    fun goBack(): Boolean {
        return if (webView.canGoBack()) {
            webView.goBack()
            true
        } else {
            false
        }
    }

    /** 현재 웹뷰 객체 반환 */
    fun getCurrentWebView(): BaseWebView {
        return webView
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        // 웹뷰 메모리 해제
        webView.destroy()
        super.onDestroyView()
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<out Uri?>?>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean {
        LogUtil.i(LogUtil.WEB_VIEW_LOG_TAG, "onShowFileChooser() : ${fileChooserParams?.acceptTypes}")

        return true
    }

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        url: String?
    ): Boolean {
        return false
    }

    override fun onPageStarted(
        view: WebView?,
        url: String?,
        favicon: Bitmap?
    ) {}

    override fun onPageFinished(view: WebView?, url: String?) {}

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {}
}