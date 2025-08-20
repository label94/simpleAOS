package co.aos.splash.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState

/**
 * 스플래시 관련 기능 명세서
 * */
class SplashContract {

    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 초기 init */
        data object InitSettingLoad: Event()

        /** 로그인 화면 이동 */
        data object MoveLoginPage: Event()

        /** 안내 화면 이동 */
        data object MoveGuidePage: Event()
    }

    /** 상태 정의 */
    data class State(
        val isShowSplash: Boolean = true,
        val isFirstLaunch: Boolean = false,
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect {
        /** 로그인 화면 이동 */
        data object MoveLoginPage: Effect()

        /** 안내 화면 이동 */
        data object MoveGuidePage: Effect()
    }
}