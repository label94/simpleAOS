package co.aos.home.inspiration.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState

/** 오늘의 영감 관련 명세서 */
class InspirationContract {

    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 초기 진입 시 이벤트 */
        data object InitEvent: Event()

        /** 기분 점수 선택 이벤트 */
        data class OnMoodSelected(val mood: Int?): Event()

        /** 관심 키워드 입력 값 변경(텍스트 필드) */
        data class OnHintChanged(val hint: String): Event()

        /** 프롬프트 실행 이벤트 */
        data object OnLaunchedPrompts: Event()

        /** 프롬프트 결과 카드 클릭 이벤트 */
        data class OnPromptResultClicked(val result: String): Event()

        /** 프롬프트 생성 버튼 활성화 유무 이벤트 */
        data class OnUpdateIsShowLaunchedPromptsBtn(val isShowLaunchedPromptsBtn: Boolean): Event()
    }

    /** 상태 정의 */
    data class State(
        val loading: Boolean = false,        // 로딩 중 여부
        val prompts: List<String> = emptyList(), // 생성된 프롬프트 리스트
        val error: String? = null,          // 에러 메시지
        val selectedMood: Int? = null,      // 선택된 기분 점수(1..5)
        val hint: String = "",               // 관심 키워드 입력값
        val isShowLaunchedPromptsBtn: Boolean = true, // 프롬프트 생성 버튼 클릭 여부
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect {
        /** 선택한 프롬프트를 가지고 다이어리 작성 화면으로 이동 */
        data class NavigateToWrite(val preset: String) : Effect()

        /** SnackBar 등으로 메시지 표시 */
        data class Toast(val msg: String) : Effect()
    }
}