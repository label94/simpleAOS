package co.aos.webview_feature.presentation.state

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

        /** shouldOverrideLoading 호출 시 처리하기 위한 이벤트 */
        data class ShouldOverrideLoading(val url: String?): Event()

        /** onShowFileChooser 호출 시 처리하기 위한 이벤트 */
        data class ShowFileChooser(
            val filePathCallback: ValueCallback<Array<out Uri?>?>?,
            val fileChooserParams: WebChromeClient.FileChooserParams?
        ): Event()

        /** 파일 선택 시 웹으로 보내는 uri 데이터 이벤트 */
        data class FileChooserResult(val uris: List<Uri?>?): Event()
    }

    /** 상태 정의 */
    data class State(
        /** 초기 웹뷰 설정 관련 상태 */
        val webViewConfig: WebViewConfigModel = WebViewConfigModel(
            url = "",
            userAgent = ""
        ),

        /** 파일 선택 시 웹으로 보내는 uri 데이터 상태 */
        val selectedUris: List<Uri?>? = null

    ): UiState

    /** 1회성 이벤트 처리 */
    sealed class Effect: UiEffect {
        /** 파일 탐색기 여는 이벤트를 받아서 처리하기 위함 */
        data object LaunchFileChooser: Effect()
    }
}