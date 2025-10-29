package co.aos.home.detail.update.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.home.utils.TagCatalog

/** diary 수정 관련 명세서 */
class DiaryUpdateContract {
    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 초기 init */
        data class Init(val entryId: String): Event()

        /** 제목 변경 */
        data class OnTitleChange(val title: String): Event()

        /** 내용 변경 */
        data class OnBodyChange(val v: String) : Event()

        /** 태그 선택 */
        data class OnToggleTag(val tag: String) : Event()

        /** 고정 핀 관련 선택 */
        data class OnPinnedToggle(val v: Boolean) : Event()

        /** 저장 */
        data object OnSaveClick : Event()

        /** 뒤로가기 */
        data object OnBackClick : Event()
    }

    /** 상태 정의 */
    data class State(
        val loading: Boolean = true,                // 처음 로딩 중인지
        val error: String? = null,                  // 로딩 실패 등 에러 메시지
        val entryId: String = "",                   // 현재 수정 중인 일기 ID

        val dateText: String = "",                  // yyyy-MM-dd (표시용)
        val title: String = "",
        val body: String = "",
        val availableTags: List<String> = TagCatalog.ALL, // 전체 선택지(카탈로그)
        val selectedTags: Set<String> = emptySet(),    // 현재 선택된 태그들
        val pinned: Boolean = false,                // 상단 고정 여부

        val isSaving: Boolean = false,              // 저장 중 로딩바
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect {
        /** 화면 닫기 */
        data object Close: Effect()

        /** 스낵바 토스트 */
        data class Toast(val msg: String): Effect()

        /** 저장 */
        data object Saved: Effect()
    }
}