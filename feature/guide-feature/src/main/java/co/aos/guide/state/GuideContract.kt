package co.aos.guide.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.guide.model.GuidePermissionData

/**
 * 안내 화면 기능 명세서
 * */
class GuideContract {

    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 초기 접근권한 안내 화면을 구성하기 위한 데이터 생성 */
        data object LoadGuidePermissionData: Event()

        /** 다음 단계로 넘어가기 위한 이벤트 */
        data object OnNextStep: Event()
    }

    data class State(
        val guideRequiredPermissionList: List<GuidePermissionData> = emptyList(), // 접근 권한 리스트(필수)
        val guideOptionalPermissionList: List<GuidePermissionData> = emptyList(), // 접근 권한 리스트(선택)
    ): UiState

    sealed class Effect: UiEffect {
        /** 퍼미션 요청 */
        data object RequestPermission: Effect()
    }
}