package co.aos.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import co.aos.myutils.common.AppConstants

/**
 * 웹뷰 상태 관리를 위한 웹뷰 프래그먼트
 *
 * - 메모리 안전
 * - MVI 구조에 적합
 * - WebView 히스토리, 세션, 스크롤 모두 자동 유지
 * */
class BaseWebViewFragment: Fragment() {

    /** 웹뷰 */
    private lateinit var webView: BaseWebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 웹뷰 로드 설정
        webView = BaseWebView(requireContext()).apply {
            arguments?.getString(AppConstants.WEB_LOAD_URL_KEY)?.let {
                loadWebViewUrl(it)
            }
        }
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
}