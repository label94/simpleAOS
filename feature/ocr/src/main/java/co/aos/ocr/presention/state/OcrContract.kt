package co.aos.ocr.presention.state

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.ocr.presention.model.TextResultModel

/**
 * OCR 처리를 위한 명세서
 * */
class OcrContract {
    /** 처리할 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 카메라 시작 */
        data class StartCamera(
            val lifecycleOwner: LifecycleOwner,
            val previewView: PreviewView
        ): Event()

        /** 카메라 중지 */
        data object StopCamera: Event()
    }

    /** 상태 정의 */
    data class State(
        /** 인식 후 저장되는 데이터 */
        val textResultModel: TextResultModel = TextResultModel(
            text = "",
            blocks = emptyList()
        )
    ): UiState

    /** 1회성 이벤트 처리 */
    sealed class Effect: UiEffect {
        /** snack bar 표시 */
        data class ShowSnackBar(val message: String): Effect()
    }
}