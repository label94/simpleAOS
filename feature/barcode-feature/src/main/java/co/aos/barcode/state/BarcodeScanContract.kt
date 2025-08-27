package co.aos.barcode.state

import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import com.google.mlkit.vision.common.InputImage

/**
 * barcode 스캔 관련 기능 명세서
 * */
class BarcodeScanContract {
    /** 이벤트 정의 */
    sealed class Event : UiEvent {
        /** 바코드 스캔 시작 */
        data object StartScanning : Event()

        /** 인식 시작 */
        data class BarcodeDetected(val image: InputImage) : Event()

        /** 초기화 */
        data object Reset : Event()
    }

    /** 상태 정의 */
    data class State(
        val isScanning: Boolean = false,
        val result: String? = null,
        val error: String? = null
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect : UiEffect {
        /** 스낵바 표시 */
        data class ShowSnackBar(val message: String) : Effect()
    }
}