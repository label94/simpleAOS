package co.aos.home.editor.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.home.utils.TagCatalog

/** 다이어리 작성 관련 명세서 정의 */
class DiaryEditorContract {
    /** 이벤트 정의 */
    sealed class Event : UiEvent {
        /** 타이틀 작성 및 변경 */
        data class OnTitleChange(val title: String) : Event()

        /** 본문 작성 및 변경 */
        data class OnBodyChange(val body: String) : Event()

        /** 무드 선택 */
        data class OnMoodPick(val mood: Int?) : Event()

        /** 사전 정의 태그 토글 */
        data class OnToggleTag(val tag: String) : Event()

        /** 상단 고정 토글 */
        data class OnPinnedToggle(val v: Boolean) : Event()

        /** 작성/저장 클릭 */
        data object OnSaveClick : Event()

        /** 뒤로가기 */
        data object OnBackClick : Event()
    }

    /** 상태 정의 */
    data class State(
        val dateText: String = "",              // yyyy-MM-dd (표시용)
        val title: String = "",
        val body: String = "",
        val mood: Int? = null,                  // 1..5 or null
        val availableTags: List<String> = TagCatalog.ALL, // 사전 정의 태그
        val selectedTags: Set<String> = emptySet(),    // 선택된 태그(다중)
        val pinned: Boolean = false,            // 상단 고정
        val isSaving: Boolean = false,          // 저장 중 로딩 표시
        val error: String? = null               // 오류 메시지(옵션)
    ) : UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect : UiEffect {
        /** 화면 닫기 */
        data object Close : Effect()

        /** 저장 후 닫기 */
        data object SavedAndClose: Effect()

        /** 스낵바/토스트 */
        data class Toast(val msg: String) : Effect()
    }
}