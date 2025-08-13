package co.aos.ocr.presention.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.newocr.NewOcrRecognizeTextUseCase
import co.aos.myutils.log.LogUtil
import co.aos.ocr.presention.state.NewOcrContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 신규 OCR 관련 - 뷰모델
 * */
@HiltViewModel
class NewOcrViewModel @Inject constructor(
    private val recognizeTextUseCase: NewOcrRecognizeTextUseCase
): BaseViewModel<NewOcrContract.Event, NewOcrContract.State, NewOcrContract.Effect>() {

    /** 초기 상태 정의 */
    override fun createInitialState(): NewOcrContract.State {
        return NewOcrContract.State()
    }

    /** 이벤트 제어 */
    override fun handleEvent(event: NewOcrContract.Event) {
        when(event) {
            is NewOcrContract.Event.OnCaptureBitmap -> {
                onCaptureBitmap(event.bitmap)
            }
            is NewOcrContract.Event.OnCancelCapture -> {
                clearResult()
            }
            is NewOcrContract.Event.UpdateLoading -> {
                setState { copy(isLoading = event.isLoading) }
            }
            is NewOcrContract.Event.UpdateLastBitmap -> {
                setState { copy(lastBitmap = event.bitmap) }
            }
            is NewOcrContract.Event.UpdateOcrResult -> {
                setState { copy(result = event.result) }
            }
            is NewOcrContract.Event.UpdateError -> {
                setState { copy(error = event.error) }
            }
            is NewOcrContract.Event.UpdateIsCameraGranted -> {
                setState { copy(isCameraGranted = event.isGranted) }
            }
        }
    }

    /** OCR 관련 처리 */
    private fun onCaptureBitmap(bitmap: Bitmap) {
        // 로딩 중임을 명시하는 상태로 요청
        setEvent(NewOcrContract.Event.UpdateLoading(true))

        // bit map 상태 업데이트 요청
        setEvent(NewOcrContract.Event.UpdateLastBitmap(bitmap))

        // error 상태 업데이트 요청
        setEvent(NewOcrContract.Event.UpdateError(null))

        viewModelScope.launch {
            try {
                val result = recognizeTextUseCase(bitmap)

                // 결과 데이터 업데이트 요청
                setEvent(NewOcrContract.Event.UpdateOcrResult(result))

                // 결과 업데이트 후 로딩 완료 상태로 요청
                setEvent(NewOcrContract.Event.UpdateLoading(false))
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.e(LogUtil.OCR_LOG_TAG, "error : ${e.message}")

                // 에러 상태 업데이트 요청
                setEvent(NewOcrContract.Event.UpdateError(e.message))

                // 로딩 완료 상태로 요청
                setEvent(NewOcrContract.Event.UpdateLoading(false))
            }
        }
    }

    /** 모든 상태 초기화 */
    private fun clearResult() {
        setEvent(NewOcrContract.Event.UpdateLoading(false))
        setEvent(NewOcrContract.Event.UpdateLastBitmap(null))
        setEvent(NewOcrContract.Event.UpdateOcrResult(null))
        setEvent(NewOcrContract.Event.UpdateError(null))
    }
}