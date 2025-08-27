package co.aos.domain.usecase.barcode.impl

import co.aos.domain.model.BarcodeResult
import co.aos.domain.repository.BarcodeScannerRepository
import co.aos.domain.usecase.barcode.ProcessBarcodeUseCase
import com.google.mlkit.vision.common.InputImage
import javax.inject.Inject

/**
 * 바코드 스캔 관련 유스케이스 구현 클래스
 * */
class ProcessBarcodeUseCaseImpl @Inject constructor(
    private val barcodeScannerRepository: BarcodeScannerRepository
): ProcessBarcodeUseCase {
    override suspend fun invoke(image: InputImage): BarcodeResult {
        return barcodeScannerRepository.scan(image)
    }
}