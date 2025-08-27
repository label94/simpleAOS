package co.aos.data.repository

import co.aos.domain.model.BarcodeResult
import co.aos.domain.repository.BarcodeScannerRepository
import co.aos.myutils.log.LogUtil
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * 바코드 관련 Repository 구현 클래스
 * */
class BarcodeScannerRepositoryImpl @Inject constructor() : BarcodeScannerRepository {
    private var scanner: BarcodeScanner? = null

    private fun getScanner(): BarcodeScanner {
        if (scanner == null) {
            scanner = BarcodeScanning.getClient()
        }
        return scanner!!
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun scan(image: InputImage): BarcodeResult {
        return suspendCancellableCoroutine { cont ->
            scanner = getScanner()
            scanner?.process(image)
                ?.addOnSuccessListener { barcodes ->
                    val barcode = barcodes.firstOrNull()
                    LogUtil.i(LogUtil.BARCODE_SCAN_LOG_TAG, "barcode : $barcode")

                    if (barcode?.rawValue != null) {
                        cont.resume(BarcodeResult(barcode.rawValue ?: ""))
                    } else {
                        cont.resume(BarcodeResult(""))
                    }
                }?.addOnFailureListener {
                    LogUtil.e(LogUtil.BARCODE_SCAN_LOG_TAG, "barcode scan error : $it")
                    cont.resume(BarcodeResult(""))
                }
        }
    }

    override fun stopScanner() {
        scanner?.close()
        scanner = null
    }
}