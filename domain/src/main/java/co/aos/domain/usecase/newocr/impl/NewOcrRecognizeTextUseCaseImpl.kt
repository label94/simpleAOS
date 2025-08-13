package co.aos.domain.usecase.newocr.impl

import android.graphics.Bitmap
import co.aos.domain.model.NewOcrResult
import co.aos.domain.repository.NewOcrRepository
import co.aos.domain.usecase.newocr.NewOcrRecognizeTextUseCase
import javax.inject.Inject

/**
 * 신규 OCR 관련 - OCR 처리 UseCase 구현체
 * */
class NewOcrRecognizeTextUseCaseImpl @Inject constructor(
    private val repository: NewOcrRepository
): NewOcrRecognizeTextUseCase {
    /** OCR 처리 */
    override suspend fun invoke(bitmap: Bitmap): NewOcrResult {
        return repository.recognizeFromBitMap(bitmap)
    }
}