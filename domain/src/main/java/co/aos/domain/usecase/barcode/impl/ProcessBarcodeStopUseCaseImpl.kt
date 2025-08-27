package co.aos.domain.usecase.barcode.impl

import co.aos.domain.repository.BarcodeScannerRepository
import co.aos.domain.usecase.barcode.ProcessBarcodeStopUseCase
import javax.inject.Inject

class ProcessBarcodeStopUseCaseImpl @Inject constructor(
    private val barcodeScannerRepository: BarcodeScannerRepository
): ProcessBarcodeStopUseCase {
    override fun invoke() {
        barcodeScannerRepository.stopScanner()
    }
}