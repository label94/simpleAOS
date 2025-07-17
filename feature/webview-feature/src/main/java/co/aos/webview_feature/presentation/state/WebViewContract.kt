package co.aos.webview_feature.presentation.state

import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.webview_feature.presentation.model.WebViewConfigModel

/**
 * 웹뷰 관련 기능을 정의한 명세서
 * */
class WebViewContract {

    /** 처리할 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 초기 웹뷰 설정 이벤트 */
        data object InitWebViewConfig: Event()

        /** 로드할 웹뷰 URL 업데이트 */
        data class UpdateLoadWebViewUrl(val url: String): Event()

        /** shouldOverrideLoading 호출 시 처리하기 위한 이벤트 */
        data class ShouldOverrideLoading(val url: String?): Event()

        /** onShowFileChooser 호출 시 처리하기 위한 이벤트 */
        data class ShowFileChooser(
            val filePathCallback: ValueCallback<Array<out Uri?>?>?,
            val fileChooserParams: WebChromeClient.FileChooserParams?
        ): Event()

        /** 파일 선택 시 웹으로 보내는 uri 데이터 이벤트 */
        data class FileChooserResult(val resultCode: Int, val intent: Intent?): Event()

        /** 카메라 권한 처리 후 파일 탐색기 열기 위한 이벤트 */
        data class ReOpenFileChooser(val isGrantedCameraPermission: Boolean): Event()

        /** 앱 실행 된 상태에서 외부에서 변경된 URL이 전달 될 경우 */
        data object ReLoadWebUrl: Event()
    }

    /** 상태 정의 */
    data class State(
        /** 초기 웹뷰 설정 관련 상태 */
        val webViewConfig: WebViewConfigModel = WebViewConfigModel(
            userAgent = ""
        ),

        /** 웹뷰 로드할 URL */
        val url: String? = null
    ): UiState

    /** 1회성 이벤트 처리 */
    sealed class Effect: UiEffect {
        /** 파일 탐색기 여는 이벤트를 받아서 처리하기 위함 */
        data class LaunchFileChooser(val intent: Intent): Effect()

        /** 카메라 권한 요청 */
        data object RequestCameraPermission: Effect()

        /** 업데이트 한 url로 웹뷰 로드 */
        data class ReLoadWebViewUrl(val url: String): Effect()
    }
}