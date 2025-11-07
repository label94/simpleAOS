package co.aos.home.calendar.viewmodel

import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.model.DiaryListItem
import co.aos.domain.usecase.diary.GetDiarySortListByDateUseCase
import co.aos.domain.usecase.diary.GetDiarySortListByMonthUseCase
import co.aos.domain.usecase.user.renewal.GetCurrentUserUseCase
import co.aos.home.calendar.state.CalendarContract
import co.aos.myutils.log.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * 달력 ViewModel
 * - 상단 : 연/월 네비 + 달력(오늘/선택 강조)
 * - 하단 : 선택 날짜의 일기 목록(태그, 제목 형식)
 * */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val authUseCase: GetCurrentUserUseCase,
    private val getDiarySortByMonthUseCase: GetDiarySortListByMonthUseCase,
    private val getDiarySortByDateUseCase: GetDiarySortListByDateUseCase
): BaseViewModel<CalendarContract.Event, CalendarContract.State, CalendarContract.Effect>() {

    init {
        // 초기 로드
        setEvent(CalendarContract.Event.Load(YearMonth.now()))
    }

    /** 초기 상태 정의 */
    override fun createInitialState(): CalendarContract.State {
        return CalendarContract.State()
    }

    /** 이벤트 제어 */
    override fun handleEvent(event: CalendarContract.Event) {
        when(event) {
            is CalendarContract.Event.Load -> {
                fetchInitLoad(event.ym)
            }
            is CalendarContract.Event.Today -> {
                fetchToday()
            }
            is CalendarContract.Event.OnPrevYear -> {
                fetchPrevYear()
            }
            is CalendarContract.Event.OnNextYear -> {
                fetchNextYear()
            }
            is CalendarContract.Event.OnPrevMonth -> {
                fetchPrevMonth(event.current)
            }
            is CalendarContract.Event.OnNextMonth -> {
                fetchNextMonth(event.current)
            }
            is CalendarContract.Event.OnSelectMonth -> {
                selectMonth(event.month)
            }
            is CalendarContract.Event.OnDayClick -> {
                selectDate(event.day)
            }
            is CalendarContract.Event.OnEntryClick -> {
                moveDiaryDetail(event.id)
            }
        }
    }

    /** 초기 달력 셋팅 */
    private fun fetchInitLoad(ym: YearMonth) {
        val today = LocalDate.now()
        setState {
            copy(
                today = today,
                displayYear = ym.year,
                selectedDate = today
            )
        }
        loadMonth(ym)
    }

    /**
     * 월 데이터 로드
     * - 달력 도트 생성
     * - 선택 날짜 보정 후 하단 목록 로드
     * */
    private fun loadMonth(
        ym: YearMonth,
        preferredSelect: LocalDate? = null
    ) {
        LogUtil.d(LogUtil.CALENDAR_LOG_TAG, "load month => $ym , preferredSelect : $preferredSelect")

        viewModelScope.launch {
            try {
                val uid = authUseCase.invoke()?.uid
                if (uid.isNullOrEmpty()) {
                    setEffect(CalendarContract.Effect.Toast("유저 정보가 없습니다."))
                    return@launch
                }

                setState { copy(loading = true, error = null) }

                // 1) 해당 월의 요약 항목 로드
                val monthItems = getDiarySortByMonthUseCase.invoke(uid, ym)
                LogUtil.d(LogUtil.CALENDAR_LOG_TAG, "month items => $monthItems")

                // 2) 날짜 별 작성 갯수
                val countMap = mutableMapOf<LocalDate, Int>()
                monthItems.forEach { item ->
                    countMap[item.date] = (countMap[item.date] ?: 0) + 1
                }

                // 3) 6x7(42칸) 그리드 생성 (일요일 시작)
                val days = buildMonthGrid(ym, startOn = DayOfWeek.SUNDAY)

                // 4) 선택일 보정 우선 순위(preferred -> 기존 selected -> today -> 1일)
                val selected = when {
                    preferredSelect != null && days.contains(preferredSelect) -> preferredSelect
                    days.contains(currentState.selectedDate) -> currentState.selectedDate
                    days.contains(currentState.today) -> currentState.today
                    else -> ym.atDay(1)
                }
                LogUtil.d(LogUtil.CALENDAR_LOG_TAG, "selected date => $selected")
                LogUtil.i(LogUtil.CALENDAR_LOG_TAG, "dayCount => $countMap")

                // 달력 관련 데이터 상태 업데이트
                setState {
                    copy(
                        loading = false,
                        currentMonth = ym,
                        monthDays = days,
                        dayCount = countMap,
                        selectedDate = selected,
                        entriesOfSelectedDay = emptyList(),
                        displayYear = ym.year
                    )
                }

                // 5) 하단 영역 : 선택한 날에 표시할 달력 데이터
                loadDay(selected) { items ->
                    LogUtil.d(LogUtil.CALENDAR_LOG_TAG, "day date => $items")
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                LogUtil.e(LogUtil.CALENDAR_LOG_TAG, "error => ${t.message}")

                setState { copy(loading = false, error = t.message) }
                setEffect(CalendarContract.Effect.Toast("불러오기 실패"))
            }
        }
    }

    /** 6x7 달력 그리드 생성 (startOn=일요일) */
    private fun buildMonthGrid(
        ym: YearMonth,
        startOn: DayOfWeek
    ): List<LocalDate> {
        // 해당 월의 1일 날짜를 구함
        val first = ym.atDay(1)

        // 해당 월의 1일이 무슨 요일인지 알아내고, 그 요일의 인덱스 값을 계산
        // 이떼 달력 시작 요일(startOn) 에 따라 인덱스가 달라짐(idx 함수가 해당 역할 수행)
        val leading = idx(first.dayOfWeek, startOn)

        // 달력의 첫번째 칸에 표시될 날짜를 계산
        // '1일' 에서 '1일의 요일 인덱스 만큼 '날짜' 를 뒤로 설정
        val start = first.minusDays(leading.toLong())

        // 시작 날짜부터 42일(6주 x 7일) 동안의 날짜 목록을 생성
        return (0 until 42).map { start.plusDays(it.toLong()) }
    }

    /**
     * 달력 생성 시 해당 월의 1일이 시작하는 정확한 요일 위치를 설정하기 위해 사용
     * */
    private fun idx(dow: DayOfWeek, startOn: DayOfWeek) = when (startOn) {
        DayOfWeek.SUNDAY -> when (dow) {
            DayOfWeek.SUNDAY -> 0; DayOfWeek.MONDAY -> 1; DayOfWeek.TUESDAY -> 2
            DayOfWeek.WEDNESDAY -> 3; DayOfWeek.THURSDAY -> 4
            DayOfWeek.FRIDAY -> 5; DayOfWeek.SATURDAY -> 6
        }
        else -> when (dow) {
            DayOfWeek.MONDAY -> 0; DayOfWeek.TUESDAY -> 1; DayOfWeek.WEDNESDAY -> 2
            DayOfWeek.THURSDAY -> 3; DayOfWeek.FRIDAY -> 4; DayOfWeek.SATURDAY -> 5
            DayOfWeek.SUNDAY -> 6
        }
    }

    /** 특정 일자 목록 로드(완료 후 콜백으로 전달) */
    private fun loadDay(
        day: LocalDate,
        onLoaded: (List<DiaryListItem>) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val uid = authUseCase.invoke()?.uid
                if (uid.isNullOrEmpty()) {
                    setEffect(CalendarContract.Effect.Toast("유저 정보가 없습니다."))
                    return@launch
                }

                // 해당 날짜에 맞는 일기 목록 로드
                val items = getDiarySortByDateUseCase.invoke(uid, day)
                setState { copy(entriesOfSelectedDay = items) }
                onLoaded(items)
            } catch (t: Throwable) {
                t.printStackTrace()
                LogUtil.e(LogUtil.CALENDAR_LOG_TAG, "error => ${t.message}")

                setEffect(CalendarContract.Effect.Toast("해달 날짜 데이터 불러오기 실패"))
            }
        }
    }

    /** 오늘로 점프 (월 다르면 월 이동 후 해당 일 로드) */
    private fun fetchToday() {
        val today = currentState.today
        setState { copy(selectedDate = today) }

        val ymOfToday = YearMonth.from(today)
        if (ymOfToday != currentState.currentMonth) {
            loadMonth(ymOfToday, preferredSelect = today)
        } else {
            loadDay(today)
        }
    }

    /** 월 이동(이전 달) */
    private fun fetchPrevMonth(ym: YearMonth) {
        val prev = ym.minusMonths(1)
        setState { copy(displayYear = prev.year) }
        setState { copy(selectedDate = prev.atDay(1)) }
        loadMonth(prev)
    }

    /** 월 이동(다음 달) */
    private fun fetchNextMonth(ym: YearMonth) {
        val next = ym.plusMonths(1)
        setState { copy(displayYear = next.year) }
        setState { copy(selectedDate = next.atDay(1)) }
        loadMonth(next)
    }

    /** 년도 이동(이전 년도) */
    private fun fetchPrevYear() {
        // 타겟 년도 가져오기
        val newYear = currentState.displayYear - 1

        // 목표 연월 생성(새 연도와 '현재 월'을 조합)
        val targetYm = YearMonth.of(newYear, currentState.currentMonth.monthValue)

        // 날짜 보정(2월 29일 같은 예외상황을 처리)
        // ex) 2024년도 2월 29일 보고 있다가 이전 연도로 가면 2023년도 2월이 되는 때 이 때 29일 없기 때문에
        // 해당 년도에 맞는 월에서 마지막 날로 날짜를 자동으로 변경
        val preservedDay = currentState.selectedDate.dayOfMonth
            .coerceAtMost(targetYm.lengthOfMonth())

        // 최종 선택일 계산
        val preferred = currentState.selectedDate.withYear(newYear)
            .withMonth(targetYm.monthValue)
            .withDayOfMonth(preservedDay)
        setState { copy(displayYear = newYear) }
        loadMonth(targetYm, preferredSelect = preferred)
    }

    /** 년도 이동(다음 년도) */
    private fun fetchNextYear() {
        val newYear = currentState.displayYear + 1
        val targetYm = YearMonth.of(newYear, currentState.currentMonth.monthValue)
        val preservedDay = currentState.selectedDate.dayOfMonth
            .coerceAtMost(targetYm.lengthOfMonth())
        val preferred = currentState.selectedDate.withYear(newYear)
            .withMonth(targetYm.monthValue)
            .withDayOfMonth(preservedDay)
        setState { copy(displayYear = newYear) }
        loadMonth(targetYm, preferredSelect = preferred)
    }

    /** 특정 월 클릭 */
    private fun selectMonth(month: Int) {
        val targetYm = YearMonth.of(currentState.displayYear, month.coerceIn(1, 12))
        loadMonth(targetYm)
    }

    /**
     * 날짜 선택
     * - 하단 목록 로드
     * */
    private fun selectDate(date: LocalDate) {
        setState { copy(selectedDate = date) }
        loadDay(date)
    }

    /** 상세 이동 */
    private fun moveDiaryDetail(id: String) {
        setEffect(CalendarContract.Effect.NavigateToDetail(id))
    }
}