package co.aos.webview_feature.presentation.state

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
    }

    /** 상태 정의 */
    data class State(
        val webViewConfig: WebViewConfigModel = WebViewConfigModel(
            url = "",
            userAgent = ""
        ),
    ): UiState

    /** 1회성 이벤트 처리 */
    sealed class Effect: UiEffect
}