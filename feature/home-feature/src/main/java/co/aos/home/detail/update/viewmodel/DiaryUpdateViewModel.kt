package co.aos.home.detail.update.viewmodel

import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.model.DiaryEntryUpdate
import co.aos.domain.usecase.diary.GetDiaryUseCase
import co.aos.domain.usecase.diary.UpdateDiaryUseCase
import co.aos.domain.usecase.user.renewal.GetCurrentUserUseCase
import co.aos.home.detail.update.state.DiaryUpdateContract
import co.aos.myutils.log.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/** 다이어리 수정 관련 뷰모델 */
@HiltViewModel
class DiaryUpdateViewModel @Inject constructor(
    private val authUseCase: GetCurrentUserUseCase,
    private val getDetailUseCase: GetDiaryUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
): BaseViewModel<DiaryUpdateContract.Event, DiaryUpdateContract.State, DiaryUpdateContract.Effect>() {

    /** 초기 상태 설정 */
    override fun createInitialState(): DiaryUpdateContract.State {
        return DiaryUpdateContract.State()
    }

    /** 이벤트 제어 */
    override fun handleEvent(event: DiaryUpdateContract.Event) {
        when(event) {
            is DiaryUpdateContract.Event.Init -> {
                loadEntry(event.entryId)
            }
            is DiaryUpdateContract.Event.OnBackClick -> {
                setEffect(DiaryUpdateContract.Effect.Close)
            }
            is DiaryUpdateContract.Event.OnTitleChange -> {
                setState { copy(title = event.title) }
            }
            is DiaryUpdateContract.Event.OnBodyChange -> {
                setState { copy(body = event.v) }
            }
            is DiaryUpdateContract.Event.OnToggleTag -> {
                val next = currentState.selectedTags.toMutableSet().apply {
                    if (contains(event.tag)) {
                        remove(event.tag)
                    } else {
                        add(event.tag)
                    }
                }
                setState { copy(selectedTags = next) }
            }
            is DiaryUpdateContract.Event.OnPinnedToggle -> {
                setState { copy(pinned = event.v) }
            }
            is DiaryUpdateContract.Event.OnSaveClick -> {
                save()
            }
        }
    }

    /** entryId 에 맞는 다이어리 정보 로드 */
    private fun loadEntry(entryId: String) {
        viewModelScope.launch {
            val uid = authUseCase.invoke()?.uid
            if (uid.isNullOrEmpty() || entryId.isEmpty()) {
                LogUtil.e(LogUtil.DIARY_UPDATE_LOG_TAG, "error => uid, entryId is null or empty")
                setEffect(DiaryUpdateContract.Effect.Toast("로그인 필요한 서비스 입니다."))

                // 0.7초 후 종료
                delay(700)
                setEffect(DiaryUpdateContract.Effect.Close)
                return@launch
            }

            try {
                // DiaryDetail 데이터 로드
                val detail = getDetailUseCase.invoke(uid, entryId)
                if (detail == null) {
                    LogUtil.e(LogUtil.DIARY_UPDATE_LOG_TAG, "error => detail is null")
                    setEffect(DiaryUpdateContract.Effect.Toast("데이터가 없습니다."))
                    setEffect(DiaryUpdateContract.Effect.Close)
                    return@launch
                }

                setState {
                    copy(
                        loading = false,
                        error = null,
                        entryId = detail.id ?: "",
                        dateText = detail.date.toString(),
                        title = detail.title,
                        body = detail.body,
                        selectedTags = detail.tags.toSet(),
                        pinned = detail.pinned
                    )
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                setState { copy(loading = false, error = t.message ?: "불러오기 실패") }
                setEffect(DiaryUpdateContract.Effect.Toast("불러오기 실패"))
                setEffect(DiaryUpdateContract.Effect.Close)
            }
        }
    }

    /** 수정 내용 저장 처리 */
    private fun save() {
        viewModelScope.launch {
            val uid = authUseCase.invoke()?.uid
            if (uid.isNullOrEmpty()) {
                LogUtil.e(LogUtil.DIARY_UPDATE_LOG_TAG, "error => uid is null or empty")
                setEffect(DiaryUpdateContract.Effect.Toast("로그인 필요한 서비스 입니다."))

                // 0.7초 후 종료
                delay(700)
                setEffect(DiaryUpdateContract.Effect.Close)
                return@launch
            }

            // 본문 유효성 검증
            if (currentState.body.isEmpty()) {
                setEffect(DiaryUpdateContract.Effect.Toast("내용을 입력해주세요."))
                return@launch
            }

            try {
                setState { copy(isSaving = true) }

                // 제목이 비어 있으면 본문 첫 줄 -> "제목 없음"
                val computedTitle = currentState.title.ifBlank {
                    currentState.body.lineSequence().firstOrNull()?.take(50)?.trim().orEmpty()
                }.ifBlank { "제목 없음" }

                // 날짜는 이미 state.dateText(yyyy-MM-dd) 형태로 들어있음
                // LocalDate로 다시 변환해서 전달
                val date = try {
                    LocalDate.parse(currentState.dateText)
                } catch (_: Throwable) {
                    LocalDate.now()
                }

                // 업데이트 모델 구성
                val updateModel = DiaryEntryUpdate(
                    title = computedTitle,
                    body = currentState.body,
                    tags = currentState.selectedTags.toList(),
                    date = date,
                    pinned = currentState.pinned
                )

                // 업데이트
                updateDiaryUseCase.invoke(
                    uid = uid,
                    entryId = currentState.entryId,
                    update = updateModel
                )

                setEffect(DiaryUpdateContract.Effect.Toast("수정되었어요."))
                setEffect(DiaryUpdateContract.Effect.Saved) // 저장 완료 이벤트
            } catch (t: Throwable) {
                t.printStackTrace()
                LogUtil.e(LogUtil.DIARY_UPDATE_LOG_TAG, "error => ${t.message}")

                setState { copy(isSaving = false) }
                setEffect(DiaryUpdateContract.Effect.Toast("저장 실패했습니다."))
            }
        }
    }
}