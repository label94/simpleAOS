package co.aos.domain.model

/**
 * 신규 OCR 관련 - OCR 결과 data model
 * */
data class NewOcrResult(
    val fullText: String,
    val blocks: List<NewOcrTextBlockModel>
)
