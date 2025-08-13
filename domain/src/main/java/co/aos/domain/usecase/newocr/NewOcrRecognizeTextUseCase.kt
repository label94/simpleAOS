package co.aos.domain.usecase.newocr

import android.graphics.Bitmap
import co.aos.domain.model.NewOcrResult

/**
 * 신규 OCR 관련 - OCR 처리 UseCase
 * */
interface NewOcrRecognizeTextUseCase {
    /** OCR 처리 */
    suspend operator fun invoke(bitmap: Bitmap): NewOcrResult
}