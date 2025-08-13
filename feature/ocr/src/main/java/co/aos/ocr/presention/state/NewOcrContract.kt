package co.aos.ocr.presention.state

import android.graphics.Bitmap
import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.domain.model.NewOcrResult

/**
 * 신규 OCR 관련 - 기능 명세서
 * */
class NewOcrContract {
    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 캡쳐 시작 */
        data class OnCaptureBitmap(val bitmap: Bitmap): Event()

        /** 초기화 */
        data object OnCancelCapture: Event()

        /** 로딩 업데이트 */
        data class UpdateLoading(val isLoading: Boolean): Event()

        /** lastBitmap 관련 업데이트 */
        data class UpdateLastBitmap(val bitmap: Bitmap?): Event()

        /** 결과 데이터 업데이트 */
        data class UpdateOcrResult(val result: NewOcrResult?): Event()

        /** 에러 업데이트 */
        data class UpdateError(val error: String?): Event()

        /** 카메라 관련 권한 업데이트 */
        data class UpdateIsCameraGranted(val isGranted: Boolean): Event()
    }

    /** 상태 정의 */
    data class State(
        val isCameraGranted: Boolean = false,
        val isLoading: Boolean = false,
        val lastBitmap: Bitmap? = null,
        val result: NewOcrResult? = null,
        val error: String? = null
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect
}