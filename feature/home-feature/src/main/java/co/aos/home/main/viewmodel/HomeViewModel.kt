package co.aos.home.main.viewmodel

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
                val next = currentState.selectedTags.toMutableSet().apply {
                    if (contains(event.tag)) {
                        remove(event.tag)
                    } else {
                        add(event.tag)
                    }
                }
                setState {
                    copy(selectedTags = next)
                }
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
                setEffect(HomeContract.Effect.NavigateToEditor)
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
               val page = getRecent.invoke(uid, pageSize = 20, cursor = null) // List<DiaryCardUi>
               val recent = page.items.map { it.toCardUi() }

               // 상태 업데이트
               setState {
                   copy(
                       weeklyMood = weekly,
                       recentEntries = recent,
                       todayWritten = recent.any { it.dateText == today.toString() },
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
}