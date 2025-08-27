package co.aos.barcode.viewmodel

import androidx.lifecycle.viewModelScope
import co.aos.barcode.state.BarcodeScanContract
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.barcode.ProcessBarcodeStopUseCase
import co.aos.domain.usecase.barcode.ProcessBarcodeUseCase
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Barcode 관련 ViewModel
 * */
@HiltViewModel
class BarcodeViewModel @Inject constructor(
    private val barcodeScannerUseCase: ProcessBarcodeUseCase,
    private val barcodeScanStopUseCase: ProcessBarcodeStopUseCase
): BaseViewModel<BarcodeScanContract.Event, BarcodeScanContract.State, BarcodeScanContract.Effect>() {

    /** 초기 상태 값 정의 */
    override fun createInitialState(): BarcodeScanContract.State {
        return BarcodeScanContract.State()
    }

    /** 이벤트 처리 */
    override fun handleEvent(event: BarcodeScanContract.Event) {
        when(event) {
            is BarcodeScanContract.Event.StartScanning -> {
                setState { copy(isScanning = true) }
            }
            is BarcodeScanContract.Event.BarcodeDetected -> {
                processBarcodeScan(event.image)
            }
            is BarcodeScanContract.Event.Reset -> {
                setState { copy(isScanning = false, result = null, error = null) }
                barcodeScanStopUseCase.invoke()
            }
        }
    }

    /** 바코드 인식 */
    private fun processBarcodeScan(image: InputImage) {
        if (!uiState.value.isScanning) return

        viewModelScope.launch {
            val result = barcodeScannerUseCase.invoke(image)
            if (result.rawValue.isNotEmpty()) {
                // 바코드 인식 성공
                setState { copy(isScanning = false, result = result.rawValue, error = null) }
            } else {
                // 바코드 인식 실패
                setState { copy(isScanning = false, result = null, error = "바코드 인식에 실패했습니다.") }
            }
        }
    }
}