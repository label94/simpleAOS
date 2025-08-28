package co.aos.barcode.state

import androidx.camera.core.ImageProxy
import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import com.google.mlkit.vision.common.InputImage

/**
 * 리얼 타임 바코드 스캔 기능 명세서
 * - 바로 바코드 이미지가 보이면, 스캔 할 수 있는 버전
 * */
class BarcodeScanRealtimeContract {
    /** 이벤트 정의 */
    sealed class Event: UiEvent {
        /** 바코드 스캔 시작 */
        data object StartScanning: Event()

        /** 스캔 화면으로 다시 이동 */
        data object BackToScanner: Event()

        /** 바코드 인식 */
        data class BarcodeDetected(val barcode: InputImage, val imageProxy: ImageProxy): Event()

        /** ImageProxy -> InputImage 로 가공 */
        data class ConverterInputImage(val imageProxy: ImageProxy): Event()
    }

    /** 상태 정의 */
    data class State(
        val currentScannerState: ScannerState = ScannerState.Idle
    ): UiState

    /** 스캐너 UI 상태 */
    sealed class ScannerState {
        /** 대기 상태 */
        data object Idle: ScannerState()

        /** 스캔 상태 */
        data object Scanning: ScannerState()

        /** 성공 */
        data class Success(val barcode: String): ScannerState()

        /** 실패 */
        data class Error(val message: String): ScannerState()
    }

    /** 1회성 이벤트 정의 */
    sealed class Effect: UiEffect {}
}