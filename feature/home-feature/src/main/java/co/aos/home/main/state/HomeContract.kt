package co.aos.home.main.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.home.main.model.DiaryCardUi

/** 홈 화면에 대한 기능 명세서 */
class HomeContract {
    /** 이벤트 정의 */
    sealed class Event : UiEvent {
        /** 화면 진입 또는 당겨서 새로고침 등: 대시보드 데이터 로드 */
        data object Load : Event()

        /** 상단 검색창 텍스트 변경 */
        data class OnSearchChanged(val q: String) : Event()

        /** 태그 칩 토글(선택/해제) */
        data class OnTagToggled(val tag: String) : Event()

        /** "빠른 작성" 영역의 텍스트 버튼 클릭 */
        data object QuickAddText : Event()

        /** 오늘 무드 선택 다이얼로그 열기 */
        data object OnPickMoodClick : Event()

        /** 오늘 무드 선택 다이얼로그 닫기 */
        data object HideMoodPicker : Event()

        /** 무드 다이얼로그에서 점수 선택(1..5) */
        data class OnMoodPicked(val score: Int) : Event()

        /** 최근 일기 카드 클릭 → 상세로 이동 */
        data class OnEntryClick(val id: String) : Event()
    }

    /** 상태 정의 */
    data class State(
        /** 상단 검색창의 현재 입력값 */
        val query: String = "",

        /** 선택된 태그들의 집합 (필터링용) */
        val selectedTags: Set<String> = emptySet(),

        /** 오늘 기록한 무드 (1..5) : 없으면 null */
        val todayMood: Int? = null,

        /** 오늘 일기 작성 여부 (간이 체크) */
        val todayWritten: Boolean = false,

        /** 연속 작성 일수 */
        val streak: Int = 0,

        /** 최고 연속 작성 일수 */
        val bestStreak: Int = 0,

        /** 최근 7일 무드 (과거→오늘 순서), 값이 없으면 null */
        val weeklyMood: List<Int?> = List(7) { null },

        /** 최근 일기 카드 리스트 (요약용) */
        val recentEntries: List<DiaryCardUi> = emptyList(),

        /** 네트워크/DB 동작 중 로딩 플래그 */
        val loading: Boolean = false,

        /** 에러 메시지(있다면 스낵바 등으로 노출) */
        val error: String? = null,

        /** 무드 선택 시트 표시 여부 */
        val showMoodPicker: Boolean = false,
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect {
        /** 작성 화면으로 네비게이션 */
        object NavigateToEditor : Effect()

        /** 상세 화면으로 네비게이션 */
        data class NavigateToDetail(val id: String) : Effect()

        /** 스낵바/토스트 등 1회성 메시지 */
        data class Toast(val msg: String) : Effect()
    }
}