package co.aos.network_error_feature.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.network_error_feature.model.NetworkStatusModel

/**
 * 네트워크 상태 관련 명세서
 * */
class NetworkStatusContract {

    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 네트워크 에러 UI를 표시하는 flag 상태 업데이트 */
        data class UpdateNetworkErrorScreen(val isShow: Boolean): Event()

        /** 웹뷰 리로드 이벤트 */
        data object ReloadWebView: Event()
    }

    /** 상태 정의 */
    data class State(
        val networkStatus: NetworkStatusModel = NetworkStatusModel(isNetworkConnected = true),
        val message: String = "",
        val isShowErrorScreen: Boolean = false
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect {
        /** 다시 시도 했을 경우 웹뷰로 이벤트 제어를 넘기는 용도 */
        data object Retry: Effect()
    }
}