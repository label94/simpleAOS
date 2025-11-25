package co.aos.domain.model

import android.graphics.Rect
import androidx.annotation.Keep

/**
 * 신규 OCR 관련 TextBlock Model
 * */
@Keep
data class NewOcrTextBlockModel(
    val text: String,
    val boundingBox: Rect,
    val confidence: Float? = null
)
