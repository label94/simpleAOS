package co.aos.home.main.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.diary.GetRecentDiaryUseCase
import co.aos.domain.usecase.diary.LoadWeeklyMoodUseCase
import co.aos.domain.usecase.diary.UpsertDailyMoodUseCase
import co.aos.domain.usecase.user.renewal.GetCurrentUserUseCase
import co.aos.home.main.model.toCardUi
import co.aos.home.main.state.HomeContract
import co.aos.myutils.log.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * 홈 관련 뷰모델
 * */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authUseCase: GetCurrentUserUseCase,
    private val getRecent: GetRecentDiaryUseCase,
    private val loadWeeklyMoodUseCase: LoadWeeklyMoodUseCase,
    private val upsertDailyMoodUseCase: UpsertDailyMoodUseCase
): BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    init {
        // init 시 load 이벤트 호출
        setEvent(HomeContract.Event.Load)
    }

    override fun createInitialState(): HomeContract.State {
        return HomeContract.State()
    }

    override fun handleEvent(event: HomeContract.Event) {
        when(event) {
            is HomeContract.Event.Load -> {
                loadDashboard()
            }
            is HomeContract.Event.OnSearchChanged -> {
                setState { copy(query = event.q) }
            }
            is HomeContract.Event.OnTagToggled -> {
                // 선택 토글 후 필터 적용
                val next = currentState.selectedTags.toMutableSet().apply {
                    if (contains(event.tag)) {
                        remove(event.tag)
                    } else {
                        add(event.tag)
                    }
                }
                applyTagFilter(next)
            }
            is HomeContract.Event.OnTagFromCardClicked -> {
                // 카드 내부 태그 클릭 -> 무조건 추가(이미 있으면 무시)
                val next = currentState.selectedTags.toMutableSet().apply { add(event.tag) }
                applyTagFilter(next)
            }
            is HomeContract.Event.OnClearTagFilters -> {
                applyTagFilter(emptySet())
            }
            is HomeContract.Event.QuickAddText -> {
                setEffect(HomeContract.Effect.NavigateToEditor)
            }
            is HomeContract.Event.OnPickMoodClick -> {
                setState { copy(showMoodPicker = true) }
            }
            is HomeContract.Event.HideMoodPicker -> {
                setState { copy(showMoodPicker = false) }
            }
            is HomeContract.Event.OnMoodPicked -> {
                saveTodayMood(event.score)
            }
            is HomeContract.Event.OnEntryClick -> {
                setEffect(HomeContract.Effect.NavigateToDetail(event.id))
            }
        }
    }

    /** 홈 대시 보드 로드(주간 무드 + 최근 일기) */
    private fun loadDashboard() {
       viewModelScope.launch {
           val uid = authUseCase.invoke()?.uid
           if (uid.isNullOrEmpty()) return@launch

           try {
               setState { copy(loading = true, error = null) }
               val today = LocalDate.now()
               val weekly = loadWeeklyMoodUseCase.invoke(uid, today) // List<Int?>

               // 최근 5개의 데이터를 가져온다.
               val page = getRecent.invoke(uid, pageSize = 5, cursor = null) // List<DiaryCardUi>
               val recents = page.items.map { it.toCardUi() }

               // 태그 수집(빈도 높은 순 -> 가나다 정렬)
               val tagFreq = mutableMapOf<String, Int>()
               recents.forEach { card ->
                   card.tags.forEach { t ->
                       tagFreq[t] = (tagFreq[t] ?: 0) + 1
                   }
               }
               val allTagSorted = tagFreq.entries
                   .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }
                       .thenBy { it.key })
                   .map { it.key }

               // 상태 업데이트
               setState {
                   copy(
                       weeklyMood = weekly,
                       recentEntries = recents,
                       filteredEntries = recents, // 초기엔 필터 없이 원본 그대로
                       allTags = allTagSorted,
                       todayWritten = recents.any { it.dateText == today.toString() },
                       todayMood = weekly.lastOrNull(),
                       loading = false
                   )
               }
           } catch (t: Throwable) {
               t.printStackTrace()
               LogUtil.e(LogUtil.HOME_LOG_TAG, "error : $t")

               setState { copy(loading = false, error = t.message) }
               setEffect(HomeContract.Effect.Toast("홈 로드 실패"))
           }
       }
    }

    /** 오늘 무드 저장 -> 주간 무드 재로딩 */
    private fun saveTodayMood(score: Int) {
        viewModelScope.launch {
            val uid = authUseCase.invoke()?.uid
            if (uid.isNullOrEmpty()) return@launch

            try {
                setState { copy(showMoodPicker = false) }

                val today = LocalDate.now()
                upsertDailyMoodUseCase.invoke(uid, today, score)
                val weekly = loadWeeklyMoodUseCase.invoke(uid, today)
                setState { copy(weeklyMood = weekly, todayMood = weekly.lastOrNull()) }
                setEffect(HomeContract.Effect.Toast("오늘 무드가 저장되었어요."))
            } catch (t: Throwable) {
                t.printStackTrace()
                LogUtil.e(LogUtil.HOME_LOG_TAG, "error : $t")

                setEffect(HomeContract.Effect.Toast("오늘 무드 저장 실패"))
            }
        }
    }

    /** 태그 필터 적용(AND 조건) */
    private fun applyTagFilter(nextSelected: Set<String>) {
        val base = currentState.recentEntries
        val filtered = if (nextSelected.isEmpty()) {
            base
        } else {
            base.filter { card -> nextSelected.all { it in card.tags } }
        }
        setState { copy(selectedTags = nextSelected, filteredEntries = filtered) }
    }
}