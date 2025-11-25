package co.aos.home.inspiration.viewmodel

import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.ai.GetDailyPromptsUseCase
import co.aos.home.inspiration.state.InspirationContract
import co.aos.myutils.log.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 오늘의 영감 관련 뷰모델 */
@HiltViewModel
class InspirationViewModel @Inject constructor(
    private val getDailyPromptsUseCase: GetDailyPromptsUseCase
): BaseViewModel<InspirationContract.Event, InspirationContract.State, InspirationContract.Effect>() {

    init {
        // 초기 init 이벤트 호출
        setEvent(InspirationContract.Event.InitEvent)
    }

    /** 초기 상태 */
    override fun createInitialState(): InspirationContract.State {
        return InspirationContract.State()
    }

    /** 이벤트 처리 */
    override fun handleEvent(event: InspirationContract.Event) {
        when(event) {
            is InspirationContract.Event.InitEvent -> {
                initData()
            }
            is InspirationContract.Event.OnMoodSelected -> {
                setState { copy(selectedMood = event.mood) }
            }
            is InspirationContract.Event.OnHintChanged -> {
                setState { copy(hint = event.hint) }
            }
            is InspirationContract.Event.OnLaunchedPrompts -> {
                launchedPrompts()
            }
            is InspirationContract.Event.OnPromptResultClicked -> {
                setEffect(InspirationContract.Effect.NavigateToWrite(event.result))
            }
            is InspirationContract.Event.OnUpdateIsShowLaunchedPromptsBtn -> {
                setState { copy(isShowLaunchedPromptsBtn = event.isShowLaunchedPromptsBtn) }
            }
        }
    }

    /** 초기화 */
    private fun initData() {
        setState {
            copy(
                loading = false,
                prompts = emptyList(),
                error = null,
                selectedMood = null,
                hint = "",
                isShowLaunchedPromptsBtn = true
            )
        }
    }

    /** 프롬프트 실행 */
    private fun launchedPrompts() {
        val mood = currentState.selectedMood
        val hint = (currentState.hint).trim()

        viewModelScope.launch {
            try {
                setState { copy(loading = true, error = null, isShowLaunchedPromptsBtn = false) }

                val prompts = getDailyPromptsUseCase(mood, hint)
                setState { copy(loading = false, prompts = prompts, error = null, isShowLaunchedPromptsBtn = false) }
                LogUtil.d(LogUtil.INSPIRATION_LOG_TAG, "prompts : $prompts")

                // 결과 없음 처리
                if (prompts.isEmpty()) {
                    setEffect(InspirationContract.Effect.Toast("지금은 영감 문장을 불러오지 못했어요. 잠시 후 다시 시도 해주세요."))
                    setEvent(InspirationContract.Event.OnUpdateIsShowLaunchedPromptsBtn(true))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.e(LogUtil.INSPIRATION_LOG_TAG, "launchedPrompts error : $e")

                setState { copy(loading = false, error = e.message, isShowLaunchedPromptsBtn = true) }
                setEffect(InspirationContract.Effect.Toast("실행 오류가 발생하였습니다."))
            }
        }
    }
}