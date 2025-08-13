package co.aos.domain.model

import android.graphics.Rect

/**
 * 신규 OCR 관련 TextBlock Model
 * */
data class NewOcrTextBlockModel(
    val text: String,
    val boundingBox: Rect,
    val confidence: Float? = null
)
