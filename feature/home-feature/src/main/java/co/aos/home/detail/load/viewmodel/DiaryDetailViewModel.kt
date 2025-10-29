package co.aos.home.detail.load.viewmodel

import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.diary.DeleteDiaryUseCase
import co.aos.domain.usecase.diary.GetDiaryUseCase
import co.aos.domain.usecase.diary.SetDiaryPinnedUseCase
import co.aos.domain.usecase.user.renewal.GetCurrentUserUseCase
import co.aos.home.detail.load.state.DiaryDetailContract
import co.aos.home.detail.model.DiaryDetail
import co.aos.myutils.log.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 다이어리 상세 화면 관련 뷰모델 */
@HiltViewModel
class DiaryDetailViewModel @Inject constructor(
    private val authUseCase: GetCurrentUserUseCase,
    private val getDetailUseCase: GetDiaryUseCase,
    private val deleteDiaryUseCase: DeleteDiaryUseCase,
    private val setPinnedUseCase: SetDiaryPinnedUseCase
): BaseViewModel<DiaryDetailContract.Event, DiaryDetailContract.State, DiaryDetailContract.Effect>() {

    private var entryId: String? = null

    /** 초기 상태 설정 */
    override fun createInitialState(): DiaryDetailContract.State {
        return DiaryDetailContract.State()
    }

    /** 이벤트 제어 */
    override fun handleEvent(event: DiaryDetailContract.Event) {
        when(event) {
            is DiaryDetailContract.Event.Load -> {
                load(event.entryId)
            }
            is DiaryDetailContract.Event.OnBackClick -> {
                setEffect(DiaryDetailContract.Effect.Close)
            }
            is DiaryDetailContract.Event.OnEditClick -> {
                entryId?.let {
                    setEffect(DiaryDetailContract.Effect.Edit(it))
                }
            }
            is DiaryDetailContract.Event.OnDeleteClick -> {
                setState { copy(showDeleteConfirm = true) }
            }
            is DiaryDetailContract.Event.OnDeleteCancel -> {
                setState { copy(showDeleteConfirm = false) }
            }
            is DiaryDetailContract.Event.OnDeleteConfirm -> {
                confirmDelete()
            }
            is DiaryDetailContract.Event.OnTogglePinned -> {
                togglePinned()
            }
            is DiaryDetailContract.Event.OnShareClick -> {
                share()
            }
            is DiaryDetailContract.Event.UpdateRequestRefresh -> {
                setState { copy(isRefresh = event.isRefresh) }
            }
        }
    }

    /** 단건 조회 */
    private fun load(id: String) {
        viewModelScope.launch {
            entryId = id
            val uid = authUseCase.invoke()?.uid ?: return@launch

            try {
                val eId = entryId ?: return@launch
                setState { copy(loading = true, error = null) }

                val detail = getDetailUseCase.invoke(uid, eId)
                if (detail == null) {
                    setState { copy(loading = false, error = "존재하지 않는 항목 입니다.") }
                    setEffect(DiaryDetailContract.Effect.Toast("항목을 찾을 수 없어요."))
                } else {
                    val data = DiaryDetail(
                        id = detail.id ?: "",
                        title = detail.title,
                        body = detail.body,
                        mood = detail.mood,
                        pinned = detail.pinned,
                        date = detail.date,
                        tags = detail.tags,
                    )

                    // 데이터 업데이트
                    setState { copy(loading = false, entry = data) }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                LogUtil.e(LogUtil.DIARY_DETAIL_LOG_TAG, "error : ${t.toString()}")

                setState { copy(loading = false, error = t.message) }
                setEffect(DiaryDetailContract.Effect.Toast("불러오기 실패하였습니다."))
            }
        }
    }

    /** 삭제 확정 */
    private fun confirmDelete() {
        viewModelScope.launch {
            val uid = authUseCase.invoke()?.uid ?: return@launch
            val id = entryId ?: return@launch

            try {
                setState { copy(showDeleteConfirm = false) }
                deleteDiaryUseCase.invoke(uid, id)

                setState { copy(isRefresh = true) } // 삭제 시 이전 화면 업데이트를 위해 flag true로 변경

                setEffect(DiaryDetailContract.Effect.Toast("삭제되었습니다."))

                setEffect(DiaryDetailContract.Effect.Close)
            } catch (t: Throwable) {
                t.printStackTrace()
                LogUtil.e(LogUtil.DIARY_DETAIL_LOG_TAG, "error : ${t.toString()}")

                setEffect(DiaryDetailContract.Effect.Toast("삭제 실패 하였습니다."))
            }
        }
    }

    /** 핀 토글 */
    private fun togglePinned() {
        viewModelScope.launch {
            val uid = authUseCase.invoke()?.uid ?: return@launch
            val s = currentState.entry ?: return@launch

            try {
                val next = !s.pinned
                setPinnedUseCase.invoke(uid, s.id, next)

                setState { copy(isRefresh = true) } // 핀 토글 변경 시 이전 화면 업데이트를 위해 flag true로 변경

                setState { copy(entry = s.copy(pinned = next)) }
            } catch (t: Throwable) {
                t.printStackTrace()
                LogUtil.e(LogUtil.DIARY_DETAIL_LOG_TAG, "error : ${t.toString()}")

                setEffect(DiaryDetailContract.Effect.Toast("핀 변경 실패하였습니다."))
            }
        }
    }

    /** 공유 텍스트 구성 */
    private fun share() {
        val e = currentState.entry ?: return
        val text = buildString {
            appendLine("제목 : ${e.title}")
            appendLine("날짜 : ${e.date}")
            if (e.tags.isNotEmpty()) appendLine("태그: ${e.tags.joinToString(", ")}")
            appendLine()
            appendLine(e.body)
        }
        setEffect(DiaryDetailContract.Effect.ShareText(text))
    }
}