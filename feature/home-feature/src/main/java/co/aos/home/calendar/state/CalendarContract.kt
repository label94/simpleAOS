package co.aos.home.calendar.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.domain.model.DiaryListItem
import java.time.LocalDate
import java.time.YearMonth

/**
 * 달력 화면 관련 기능 명세서
 * */
class CalendarContract {
    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 초기 로드: 표시할 YearMonth 지정 (대개 YearMonth.now()) */
        data class Load(val ym: YearMonth) : Event()

        /** 오늘로 점프 */
        data object Today : Event()

        /** 이전/다음 달 이동 */
        data class OnPrevMonth(val current: YearMonth) : Event()
        data class OnNextMonth(val current: YearMonth) : Event()

        /** 연도 전환 & 월 선택 (연간 네비게이션) */
        object OnPrevYear : Event()
        object OnNextYear : Event()

        /** 1..12 중 하나 선택 */
        data class OnSelectMonth(val month: Int) : Event()

        /** 날짜 선택 */
        data class OnDayClick(val day: LocalDate) : Event()

        /** 리스트 아이템 클릭 → 상세 이동 */
        data class OnEntryClick(val id: String) : Event()
    }

    /** 상태 정의 */
    data class State(
        val loading: Boolean = true,
        val error: String? = null,

        /** 오늘(초기 강조를 위해 보관) */
        val today: LocalDate = LocalDate.now(),

        /** 현재 달력에 표시 중인 YearMonth */
        val currentMonth: YearMonth = YearMonth.now(),

        /** 달력 그리드(6주=42칸) 날짜 목록 */
        val monthDays: List<LocalDate> = emptyList(),

        /** 날짜별 작성 개수 & 평균 무드 */
        val dayCount: Map<LocalDate, Int> = emptyMap(),

        /** 선택된 날짜 & 그 날의 글 목록 */
        val selectedDate: LocalDate = LocalDate.now(),
        val entriesOfSelectedDay: List<DiaryListItem> = emptyList(),

        /** 연간 패널 기준 연도 (12개월 그리드 표시 기준) */
        val displayYear: Int = YearMonth.now().year
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect {
        data class NavigateToDetail(val id: String) : Effect()
        data class Toast(val msg: String) : Effect()
    }
}