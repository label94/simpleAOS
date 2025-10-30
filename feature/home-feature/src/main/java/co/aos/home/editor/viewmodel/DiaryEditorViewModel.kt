package co.aos.home.editor.viewmodel

import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.model.DiaryEntry
import co.aos.domain.usecase.diary.AddDiaryUseCase
import co.aos.domain.usecase.user.renewal.GetCurrentUserUseCase
import co.aos.home.editor.state.DiaryEditorContract
import co.aos.myutils.log.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * 다이어리 편집 ViewModel
 * */
@HiltViewModel
class DiaryEditorViewModel  @Inject constructor(
    private val authUseCase: GetCurrentUserUseCase,
    private val addDiaryUseCase: AddDiaryUseCase,
): BaseViewModel<DiaryEditorContract.Event, DiaryEditorContract.State, DiaryEditorContract.Effect>() {

    /** 초기 상태 설정 */
    override fun createInitialState(): DiaryEditorContract.State {
        return DiaryEditorContract.State()
    }

    /** 이벤트 제어 */
    override fun handleEvent(event: DiaryEditorContract.Event) {
        when (event) {
            is DiaryEditorContract.Event.OnTitleChange -> { setState { copy(title = event.title) } }
            is DiaryEditorContract.Event.OnBodyChange -> { setState { copy(body = event.body) } }
            is DiaryEditorContract.Event.OnToggleTag -> {
                // 선택/해제 토글 (다중 선택)
                val next = currentState.selectedTags.toMutableSet().apply {
                    if (contains(event.tag)) {
                        remove(event.tag)
                    } else {
                        add(event.tag)
                    }
                }
                setState { copy(selectedTags = next) }
            }
            is DiaryEditorContract.Event.OnPinnedToggle -> { setState { copy(pinned = event.v) } }
            is DiaryEditorContract.Event.OnSaveClick -> {
                save()
            }
            is DiaryEditorContract.Event.OnBackClick -> { setEffect(DiaryEditorContract.Effect.Close) }
        }
    }

    /** 다이어리 내용 저장 */
    private fun save() {
        viewModelScope.launch {
            val uid = authUseCase.invoke()?.uid
            if (uid.isNullOrEmpty()) {
                setEffect(DiaryEditorContract.Effect.Toast("로그인이 필요합니다."))
                return@launch
            }

            if (currentState.body.isEmpty()) {
                setEffect(DiaryEditorContract.Effect.Toast("내용을 입력해주세요."))
                return@launch
            }

            try {
                setState { copy(isSaving = true, error = null) }

                // 제목이 비어 있다면, 본문 첫줄(최대 50자)을 제목으로 대체
                val computedTitle = currentState.title.ifBlank {
                    currentState.body.lineSequence().firstOrNull()?.take(50)?.trim().orEmpty()
                }.ifBlank { "제목 없음" }

                val date = try {
                    LocalDate.parse(currentState.dateText)
                } catch (_: Throwable) {
                    LocalDate.now()
                }

                // 도메인 모델 구성
                val entry = DiaryEntry(
                    title = computedTitle,
                    body = currentState.body,
                    tags = currentState.selectedTags.toList(),
                    date = date,
                    updateDate = date,
                    pinned = currentState.pinned,
                )

                // 유스케이스 호출 -> Firestore 생성 -> 새 문서 ID 반환
                addDiaryUseCase.invoke(uid, entry)

                // 상세 화면으로 네비게이션
                setEffect(DiaryEditorContract.Effect.SavedAndClose)
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.e(LogUtil.DIARY_EDITOR_LOG_TAG, "error : $e")

                setState { copy(error = e.message) }
                setEffect(DiaryEditorContract.Effect.Toast("서비스 오류가 발생하였습니다."))
                return@launch
            }
        }
    }
}