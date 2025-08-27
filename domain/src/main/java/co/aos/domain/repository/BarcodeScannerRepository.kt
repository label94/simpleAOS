package co.aos.domain.repository

import co.aos.domain.model.BarcodeResult
import com.google.mlkit.vision.common.InputImage

/**
 * 바코드 관련 Repository
 * */
interface BarcodeScannerRepository {
    /** 바코드 스캔 */
    suspend fun scan(image: InputImage): BarcodeResult

    fun stopScanner()
}