package co.aos.webview_renewal_feature.state

import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.webview_renewal_feature.consts.WebConstants

/**
 * 웹뷰 관련 기능 명세서
 * */
class WebViewContract {
    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 웹뷰 url 로드 */
        data class LoadWebViewUrl(val url: String) : Event()

        /** shouldOverride 제어 */
        data class ShouldOverrideUrlLoading(val url: String?) : Event()

        /** webView Reload 이벤트 */
        data object WebViewReLoad: Event()

        /** 뒤로가기 물리키 이벤트 */
        data class OnBackPress(
            val isWebViewCanGoBack: Boolean,
            val currentWebViewUrl: String? = null
        ) : Event()

        /** 웹뷰 Pull to Refresh 활성화/비활성화 설정 */
        data class UpdateSwipeEnable(val isSwipeEnable: Boolean) : Event()

        /** onShowFileChooser 호출 시 처리하기 위한 이벤트 */
        data class ShowFileChooser(
            val filePathCallback: ValueCallback<Array<out Uri?>?>?,
            val fileChooserParams: WebChromeClient.FileChooserParams?
        ) : Event()

        /** 파일 선택 시 웹으로 보내는 uri 데이터 이벤트 */
        data class FileChooserResult(val resultCode: Int, val intent: Intent?): Event()

        /** 카메라 권한 처리 후 파일 탐색기 열기 위한 이벤트 */
        data class ReOpenFileChooser(val isGrantedCameraPermission: Boolean): Event()
    }

    /** 상태 정의 */
    data class State(
        val loadUrl: String = WebConstants.webServerType.url,
        val isSwipeEnable: Boolean = false
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect {
        /** 웹뷰 리로드 요청 */
        data object WebViewReload: Effect()

        /** 액티비티 종료 */
        data object FinishActivity: Effect()

        /** 웹뷰 뒤로가기 */
        data object BackHistoryWebView: Effect()

        /** 파일 탐색기 여는 이벤트를 받아서 처리하기 위함 */
        data class LaunchFileChooser(val intent: Intent): Effect()

        /** 카메라 권한 요청 */
        data object RequestCameraPermission: Effect()

        /** 서브 웹 액티비티 실행 */
        data class SubWebViewOpen(val url: String): Effect()
    }
}