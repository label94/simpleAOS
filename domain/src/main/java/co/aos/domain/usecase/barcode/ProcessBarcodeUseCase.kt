package co.aos.domain.usecase.barcode

import co.aos.domain.model.BarcodeResult
import com.google.mlkit.vision.common.InputImage

/**
 * 바코드 스캔 유스케이스
 * */
interface ProcessBarcodeUseCase {
    suspend operator fun invoke(image: InputImage): BarcodeResult
}