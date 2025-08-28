package co.aos.barcode.viewmodel

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.lifecycle.viewModelScope
import co.aos.barcode.state.BarcodeScanRealtimeContract
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.barcode.ProcessBarcodeUseCase
import co.aos.myutils.log.LogUtil
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 실시간 버전 용 바코드 스캐너 뷰모델
 * */
@HiltViewModel
class BarcodeRealtimeScanViewModel @Inject constructor(
    private val barcodeScanUseCase: ProcessBarcodeUseCase
): BaseViewModel<BarcodeScanRealtimeContract.Event, BarcodeScanRealtimeContract.State, BarcodeScanRealtimeContract.Effect>() {

    // 작업 중임을 명시하는 Flag
    private var isProcessing = false

    init {
        viewModelScope.launch {
            uiState.map { it.currentScannerState }.collect {
                LogUtil.i("TestLog", "current scan state : ${it}")
            }
        }
    }

    /** 초기 상태 설정 */
    override fun createInitialState(): BarcodeScanRealtimeContract.State {
        return BarcodeScanRealtimeContract.State()
    }

    /** 이벤트 처리 */
    override fun handleEvent(event: BarcodeScanRealtimeContract.Event) {
        when(event) {
            is BarcodeScanRealtimeContract.Event.StartScanning -> {
                startScanning()
            }
            is BarcodeScanRealtimeContract.Event.BackToScanner -> {
                backToScanner()
            }
            is BarcodeScanRealtimeContract.Event.ConverterInputImage -> {
                processProxyAndSend(event.imageProxy)
            }
            is BarcodeScanRealtimeContract.Event.BarcodeDetected -> {
                barcodeDetected(event.barcode, event.imageProxy)
            }
        }
    }

    /** 바코드 스캔 시작 */
    private fun startScanning() {
        setState {
            copy(currentScannerState = BarcodeScanRealtimeContract.ScannerState.Scanning)
        }
    }

    /** 바코드 스캔 화면으로 이동 */
    private fun backToScanner() {
        setState {
            copy(currentScannerState = BarcodeScanRealtimeContract.ScannerState.Scanning)
        }
    }

    /** ImageProxy -> InputImage 형태로 가공 */
    @OptIn(ExperimentalGetImage::class)
    private fun processProxyAndSend(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val rotation = imageProxy.imageInfo.rotationDegrees
            try {
                val inputImage = InputImage.fromMediaImage(mediaImage, rotation)
                setEvent(BarcodeScanRealtimeContract.Event.BarcodeDetected(inputImage, imageProxy))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        //imageProxy.close()
    }

    /** 바코드 인식 */
    private fun barcodeDetected(inputImage: InputImage, inputImageProxy: ImageProxy) {
        LogUtil.d(LogUtil.BARCODE_SCAN_LOG_TAG, "barcodeDetected() isProcessing : $isProcessing")
        if (isProcessing) return
        isProcessing = true

        viewModelScope.launch {
            try {
                val results = barcodeScanUseCase.invoke(inputImage)
                LogUtil.d(LogUtil.BARCODE_SCAN_LOG_TAG, "barcodeDetected() results : ${results.rawValue}")

                if (results.rawValue.isNotEmpty()) {
                    // 성공 상태로 변경
                    setState {
                        copy(currentScannerState = BarcodeScanRealtimeContract.ScannerState.Success(results.rawValue))
                    }
                } else {
                    // 다시 스캔하기 위한 상태로 변경
                    setState {
                        copy(currentScannerState = BarcodeScanRealtimeContract.ScannerState.Scanning)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.e(LogUtil.BARCODE_SCAN_LOG_TAG, "barcodeDetected() Exception : ${e.message}")

                // 에러 상태로 UI 업데이트
                setState {
                    copy(currentScannerState = BarcodeScanRealtimeContract.ScannerState.Error(e.message ?: "바코드 스캔 오류"))
                }
            } finally {
                // 다시 스캔을 하기 위해 false로 변경
                isProcessing = false

                // img proxy 를 해야 리소스가 해제가 되어, 지속적으로 카메라 스캔기능을 사용할 수 있음!
                inputImageProxy.close()
            }
        }
    }
}