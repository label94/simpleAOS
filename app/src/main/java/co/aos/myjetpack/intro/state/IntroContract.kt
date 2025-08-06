package co.aos.myjetpack.intro.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState

/**
 * 인트로 관련 기능 명세서
 * */
class IntroContract {
    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 알람 권한 상태 업데이트 */
        data class UpdateNotificationPermission(val isGranted: Boolean): Event()

        /** 스플래시 화면 표시 유무 상태 업데이트 */
        data class UpdateIsShowSplash(val isShow: Boolean): Event()

        /** 최초 1회 실행 유무 상태 업데이트 */
        data class UpdateIsFirstLaunch(val isFirstLaunch: Boolean): Event()

        /** 뒤로가기 이벤트 */
        data object OnBackPressed: Event()

        /** 앱 최초 1회 실행 이후 다음 단계로 넘어가기 위한 이벤트 */
        data object OnNextStep: Event()
    }

    /** 상태 정의 */
    data class State(
        val isNotificationPermission: Boolean = false, // 알림 권한 유무
        val isShowSplash: Boolean = true, // 스플래시 표시 유무
        val isFirstLaunch: Boolean = false // 최초 실행 유무
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect {
        /** 알림 권한 체크 */
        data object CheckNotificationPermission: Effect()

        /** 액티비티 종료 */
        data object FinishActivity: Effect()
    }
}