package co.aos.webview

import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
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

/**
 * 웹뷰 상태 관리를 위한 웹뷰 프래그먼트
 *
 * - 메모리 안전
 * - MVI 구조에 적합
 * - WebView 히스토리, 세션, 스크롤 모두 자동 유지
 * */
class BaseWebViewFragment: Fragment(), BaseWebClient, BaseWebChromeClient {

    /** 웹뷰 */
    private lateinit var webView: BaseWebView

    // 콜백 리스너 - 외부에서 등록
    var onPageFinishedCallback: ((String?) -> Unit)? = null
    var onShowFileChooserCallback: ((ValueCallback<Array<out Uri?>?>?, WebChromeClient.FileChooserParams?) -> Boolean)? = null
    var onReceivedErrorCallback: ((WebResourceRequest?, WebResourceError?) -> Unit)? = null
    var onPageStartedCallback: ((String?, Bitmap?) -> Unit)? = null
    var shouldOverrideUrlLoadingCallback: ((String?) -> Boolean)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 웹뷰 로드 설정
        webView = BaseWebView(requireContext()).apply {
            // userAgent 설정
            arguments?.getString(AppConstants.WEB_LOAD_UA_KEY)?.let {
                setCustomUserAgent(it)
            }
            // 초기 로드 URL 설정
            arguments?.getString(AppConstants.WEB_LOAD_URL_KEY)?.let {
                loadWebViewUrl(it)
            }
        }
        LogUtil.i(LogUtil.WEB_VIEW_LOG_TAG, "webView Full UA : ${webView.settings.userAgentString}")

        // 디버그 타입일 때, 웹뷰 디버깅 가능하도록 설정
        if (BuildConfig.DEBUG) {
            webView.setWebViewDebugMode(true)
        }

        // 웹뷰 관련 리스너 등록
        webView.setWebViewClientInterface(this)
        webView.setWebViewChromeClientInterface(this)

        // 파일 관련 허용
        webView.setWebFileAllowAccess(true)

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

    /**
     * 웹뷰 내 페이지 전환 시 호출
     * - 웹 페이지 내 a 테그 및 href 속성을 클릭 시 호출
     * */
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        url: String?
    ): Boolean {
        return shouldOverrideUrlLoadingCallback?.invoke(url) ?: false
    }

    /** 웹뷰 페이지 로드 시작 시 호출 */
    override fun onPageStarted(
        view: WebView?,
        url: String?,
        favicon: Bitmap?
    ) {
        onPageStartedCallback?.invoke(url, favicon)
    }

    /** 웹뷰 페이지 로드 완료 시 호출 */
    override fun onPageFinished(view: WebView?, url: String?) {
        onPageFinishedCallback?.invoke(url)
    }

    /** 웹뷰 내 오류 발생 시 호출 */
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        onReceivedErrorCallback?.invoke(request, error)
    }

    /**
     * 웹에서 파일 탐색기가 호출 될 경우
     * - 파일 탐색기를 열고, 해당 파일 선택 시 웹으로 전송하는 함수
     * */
    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<out Uri?>?>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean {
        return onShowFileChooserCallback?.invoke(filePathCallback, fileChooserParams) ?: false
    }
}