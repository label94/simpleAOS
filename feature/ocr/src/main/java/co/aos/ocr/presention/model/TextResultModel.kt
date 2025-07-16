package co.aos.ocr.presention.model

import co.aos.domain.model.TextResult

/**
 * OCR 인식 관련 데이터 모델
 * */
data class TextResultModel(
    val text: String,
    val blocks: List<String>
)

fun TextResult.toPresentation(): TextResultModel =
    TextResultModel(
        text = text,
        blocks = blocks
    )
