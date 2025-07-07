package co.aos.webview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import co.aos.myutils.log.LogUtil
import co.aos.webview.utils.BaseWebChromeClient
import co.aos.webview.utils.BaseWebClient
import co.aos.webview.utils.WebUtils
import co.aos.webview.utils.WebViewUiListener

/**
 * 공통 웹뷰
 * */
class BaseWebView(
    context: Context,
    attrs: AttributeSet? = null
) : WebView(context, attrs)  {

    /** webViewClient 인터페이스 */
    private var webViewClientInterface: BaseWebClient? = null

    /** chromeClient 인터페이스 */
    private var webViewChromeClientInterface: BaseWebChromeClient? = null

    /** 웹뷰 ui 관련 인터페이스 */
    private var webViewUiListener: WebViewUiListener? = null

    /** 스와이프 레이아웃 관련 */
    var swipeRefreshLayout: SwipeRefreshLayout? = null
        set(value) {
            field = value
            field?.setOnRefreshListener {
                field?.let {
                    it.isRefreshing = false
                    webViewUiListener?.swipeRefresh(it)
                }
            }
        }

    /**
     * 초기화(웹뷰 내 설정 정의)
     * */
    init {
        // 공통 설정
        settings.apply {
            javaScriptEnabled = true // 자바스크립트 활성화(앱, 웹 통신을 위함)
            setSupportMultipleWindows(true) // 새창 띄우기 허용 여부 (window.open 지원)
            javaScriptCanOpenWindowsAutomatically = true // js 가 새창 자동으로 열 수 있도록 허용(팝업, 광고 등 제어)
            isVerticalFadingEdgeEnabled = false // 세로 페이딩 효과(스크롤 끝 부분 음영 효과) 비활성화
            isHorizontalFadingEdgeEnabled = false // 가로 페이딩 효과(스크롤 끝 부분 음영 효과) 비활성화
            isVerticalScrollBarEnabled = false // 세로 스크롤 바 미표시
            isHorizontalScrollBarEnabled = false // 가로 스크롤 바 미표시
            useWideViewPort = true // ViewPort 태그를 무시하지 않고 반영(모바일 웹 최적화 - 반응형 필수)
            builtInZoomControls = false // 줌 버튼 비활성화
            displayZoomControls = false // 화면에 줌 버튼 숨김
            loadWithOverviewMode = true // 화면 전체 너비 맞춰 로드(ViewPort와 함께 모바일 최적화)
            domStorageEnabled = true // HTML5 DOM Storage 허용(JS 기발 웹앱 필수)
            loadsImagesAutomatically = true // 이미지 자동 로드(대부분 웹앱 경우 옵션 활성화)
            allowFileAccess = false // 파일 접근 미허용(보안상 신중하게 사용)
            cacheMode = WebSettings.LOAD_NO_CACHE // 캐시 사용 안함(항상 최신 컨텐츠 로드)
            layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING // 페이지 레이아웃 방식(텍스트 자동 크기 조정)
        }

        /** WebView Client 설정 */
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                LogUtil.i(LogUtil.WEB_VIEW_LOG_TAG, "shouldOverrideUrlLoading() : $url")
                return webViewClientInterface?.shouldOverrideUrlLoading(view, url) ?: false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                LogUtil.i(LogUtil.WEB_VIEW_LOG_TAG, "onPageStarted() : $url")
                super.onPageStarted(view, url, favicon)
                webViewClientInterface?.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                LogUtil.i(LogUtil.WEB_VIEW_LOG_TAG, "onPageFinished() : $url")
                super.onPageFinished(view, url)

                // 즉각적인 쿠키 동기화를 위함
                CookieManager.getInstance().flush() // pageFinish 및 onResume 시 쿠키 동기화 필요!

                webViewClientInterface?.onPageFinished(view, url)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                LogUtil.e(LogUtil.WEB_VIEW_LOG_TAG, "onReceivedError() : $error")
                super.onReceivedError(view, request, error)
                webViewClientInterface?.onReceivedError(view, request, error)
            }
        }

        /** WebView Chrome Client 설정 */
        webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                LogUtil.d(LogUtil.WEB_VIEW_LOG_TAG, consoleMessage.toString())
                return super.onConsoleMessage(consoleMessage)
            }

            /* 파일 업로드 */
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<out Uri?>?>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                return webViewChromeClientInterface?.onShowFileChooser(webView, filePathCallback, fileChooserParams)
                    ?: super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
            }
        }

        // 쿠키 설정
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(this, true)
    }

    /**
     * Java Script 통신을 위한 bridge 추가
     * */
    @SuppressLint("JavascriptInterface")
    fun addWebViewJavascriptInterface(obj: Any, name: String) {
        addJavascriptInterface(obj, name)
    }

    /** 웹뷰 클라이언트 인터페이스 설정 */
    fun setWebViewClientInterface(webViewClientInterface: BaseWebClient) {
        this.webViewClientInterface = webViewClientInterface
    }

    /** 웹뷰 크롬 클라이언트 인터페이스 설정 */
    fun setWebViewChromeClientInterface(webViewChromeClientInterface: BaseWebChromeClient) {
        this.webViewChromeClientInterface = webViewChromeClientInterface
    }

    /**
     * 웹뷰 디버그 모드 설정
     *
     * - 크롬 인스펙터에서 웹뷰 디버그를 활성화 할 것인지 설정하는 함수
     *
     * @param isDebug : true => 웹뷰 크롬 디버그 활성화 / false => 웹뷰 크롬 디버그 비활성화
     * */
    fun setWebViewDebugMode(isDebug: Boolean) {
        setWebContentsDebuggingEnabled(isDebug)
    }

    /**
     * 웹뷰 내에서 파일 허용 설정
     *
     * - 웹뷰 내에서 파일을 허용할 것인지 설정하는 함수
     * @param isAllow : true => 웹뷰 내에서 파일 허용 / false => 웹뷰 내에서 파일 허용 안함
     * */
    fun setWebFileAllowAccess(isAllow: Boolean) {
        settings.allowFileAccess = isAllow
    }

    /**
     * SwipeRefresh 활성화 세팅
     * @param isSwipeRefreshBlock SwipeRefresh Block 여부
     */
    fun setSwipeRefreshBlock(isSwipeRefreshBlock: Boolean) {
        swipeRefreshLayout?.isEnabled = !isSwipeRefreshBlock
    }

    /**
     * 웹뷰 Url 로드
     * */
    fun loadWebViewUrl(url: String?) {
        if (url.isNullOrEmpty()) return

        // 안전한 URL 경우에만 웹뷰 URL 로드!
        if (WebUtils.isSafeUrl(url)) {
            loadUrl(url)
        } else {
            LogUtil.e(LogUtil.WEB_VIEW_LOG_TAG, "loadWebViewUrl() not safe url : $url")
        }
    }

    /**
     * 웹뷰 UserAgent 설정
     * */
    fun setCustomUserAgent(linkedHashMap: LinkedHashMap<String, String>) {
        var userAgentString = settings.userAgentString
        if(userAgentString.lastIndexOf(";") < 0) {
            userAgentString += ";"
        }

        if(linkedHashMap.isNotEmpty()) {
            val sb = StringBuilder()
            val keys = linkedHashMap.keys.iterator()

            while (keys.hasNext()) {
                val key = keys.next()
                sb.append(key).append("=").append(linkedHashMap[key])

                if (keys.hasNext()) {
                    sb.append(";")
                }
            }

            // 웹뷰에 ua 설정
            settings.userAgentString = "$userAgentString$sb;"
        }
    }
}