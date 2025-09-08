package co.aos.barcode.viewmodel

import co.aos.barcode.state.NewBarcodeScanContract
import co.aos.base.BaseViewModel
import co.aos.myutils.log.LogUtil
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 신규 바코드 뷰모델
 * */
@HiltViewModel
class NewBarcodeScanViewModel @Inject constructor(

): BaseViewModel<NewBarcodeScanContract.Event, NewBarcodeScanContract.State, NewBarcodeScanContract.Effect>() {

    init {
        setEvent(NewBarcodeScanContract.Event.StartScan)
    }

    override fun createInitialState(): NewBarcodeScanContract.State {
        return NewBarcodeScanContract.State()
    }

    override fun handleEvent(event: NewBarcodeScanContract.Event) {
        when(event) {
            is NewBarcodeScanContract.Event.StartScan -> {
                startScan()
            }
            is NewBarcodeScanContract.Event.ScanOnStop -> {
                scanOnStop()
            }
            is NewBarcodeScanContract.Event.ScanOnDetected -> {
                scanOnDetected(event.barcode)
            }
        }
    }

    /** 스캔 시작 */
    private fun startScan() {
        setState { copy(isDetected = false) }
        setState { copy(barcodeStrResult = null) }
        setState { copy(barcodeRect = null) }
    }

    /** 스캔 중지 */
    private fun scanOnStop() {
        setState { copy(isDetected = true) }
    }

    /** 스캐너 인식 */
    private fun scanOnDetected(barcode: Barcode?) {
        LogUtil.d(LogUtil.BARCODE_SCAN_LOG_TAG, "barcode : ${barcode?.rawValue}")

        setState { copy(barcodeStrResult = barcode?.rawValue) }
        setState { copy(barcodeRect = barcode?.boundingBox) }
    }
}