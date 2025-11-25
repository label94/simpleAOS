package co.aos.domain.model

import androidx.annotation.Keep

/**
 * 신규 OCR 관련 - OCR 결과 data model
 * */
@Keep
data class NewOcrResult(
    val fullText: String,
    val blocks: List<NewOcrTextBlockModel>
)
