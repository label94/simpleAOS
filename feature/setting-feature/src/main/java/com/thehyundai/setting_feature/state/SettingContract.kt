package com.thehyundai.setting_feature.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState

/**
 * 설정 화면 관련 기능 명세서
 * */
class SettingContract {

    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 초기 권한 확인 */
        data object CheckNotificationPermission: Event()

        /** 푸시 알람 업데이트 이벤트 */
        data class UpdateNotificationPermission(val isGranted: Boolean): Event()

        /** 설정 화면 종료 이벤트 */
        data object FinishSettingActivity: Event()

        /** 디바이스 설정 화면으로 이동 */
        data object MoveDeviceSetting: Event()
    }

    /** 상태 정의 */
    data class State(
        val isNotificationPermissionGranted: Boolean = false
    ): UiState

    /** 1회성 이벤트 */
    sealed class Effect: UiEffect {
        /** 설정 화면 종료 */
        data object FinishActivity: Effect()
    }
}