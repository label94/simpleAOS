package co.aos.data.repository

import co.aos.domain.model.BarcodeResult
import co.aos.domain.repository.BarcodeScannerRepository
import co.aos.myutils.log.LogUtil
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
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
            // 인식 가능한 바코드 옵션 정의
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_QR_CODE,
                    Barcode.FORMAT_CODE_128,
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_EAN_8,
                ).build()
            scanner = BarcodeScanning.getClient(options)
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
                    LogUtil.d(LogUtil.BARCODE_SCAN_LOG_TAG, "barcode : ${barcode?.rawValue}")

                    if (barcode?.rawValue != null) {
                        cont.resume(BarcodeResult(barcode.rawValue ?: ""))
                    } else {
                        cont.resume(BarcodeResult(""))
                    }
                }?.addOnFailureListener {
                    LogUtil.e(LogUtil.BARCODE_SCAN_LOG_TAG, "barcode scan error : $it")
                    cont.resume(BarcodeResult(""))
                }?.addOnCompleteListener {
                    LogUtil.d(LogUtil.BARCODE_SCAN_LOG_TAG, "barcode scan complete")
                    scanner?.close()
                    scanner = null
                }
        }
    }

    override fun stopScanner() {
        scanner?.close()
        scanner = null
    }
}