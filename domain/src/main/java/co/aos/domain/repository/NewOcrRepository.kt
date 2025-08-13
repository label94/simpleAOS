package co.aos.domain.repository

import android.graphics.Bitmap
import co.aos.domain.model.NewOcrResult

/**
 * 신규 OCR 관련 - OCR Repository
 * */
interface NewOcrRepository {
    /** 이미지 인식 */
    suspend fun recognizeFromBitMap(bitmap: Bitmap): NewOcrResult
}