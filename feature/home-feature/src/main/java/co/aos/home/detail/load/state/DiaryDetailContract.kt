package co.aos.home.detail.load.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.home.detail.model.DiaryDetail

/** 다이어리 상세 관련 명세서 정의 */
class DiaryDetailContract {

    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 첫 상세화면 데이터 로드 */
        data class Load(val entryId: String): Event()

        /** 뒤로가기 이벤트 */
        data object OnBackClick : Event()

        /** 수정으로 이동(에디터 재사용) */
        data object OnEditClick : Event()

        /** 삭제 버튼 클릭 */
        data object OnDeleteClick : Event()

        /** 삭제 확정 */
        data object OnDeleteConfirm : Event()

        /** 삭제 취소 */
        data object OnDeleteCancel : Event()

        /** 핀 토글 이벤트 */
        data object OnTogglePinned : Event()

        /** 공유 클릭 */
        data object OnShareClick : Event()

        /**
         * 이전 화면 리프레시 Flag 업데이트 요청
         * - pinned 변경, 수정 후 재로딩, 삭제 등에서 true
         * */
        data class UpdateRequestRefresh(val isRefresh: Boolean) : Event()
    }

    /** 상태 정의 */
    data class State(
        val loading: Boolean = true,
        val error: String? = null,
        val entry: DiaryDetail? = null,
        val showDeleteConfirm: Boolean = false,
        val isRefresh: Boolean = false,
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect {
        data object Close : Effect()
        data class Edit(val entryId: String) :  Effect()
        data class Toast(val msg: String) :  Effect()
        data class ShareText(val text: String) :  Effect()
    }
}