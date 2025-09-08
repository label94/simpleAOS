package co.aos.barcode.state

import android.graphics.Rect
import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import com.google.mlkit.vision.barcode.common.Barcode

/**
 * 신규 바코드 스캔 관련 기능 명세서(MLK Analyzer 사용)
 * */
class NewBarcodeScanContract {
    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 상태 업데이트 */
        data class ScanOnDetected(val barcode: Barcode?): Event()

        /** 스캔 시작 */
        data object StartScan: Event()

        /** 스캔 중지 */
        data object ScanOnStop: Event()
    }

    /** 상태 정의 */
    data class State(
        val barcodeStrResult: String? = null,
        val barcodeRect: Rect? = null,
        val isDetected: Boolean = false,
    ): UiState

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect
}