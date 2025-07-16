package co.aos.data.entity

import co.aos.domain.model.TextResult

/**
 * 인식 관련 Entity data set
 * */
data class TextResultEntity(
    val text: String,
    val blocks: List<String>
) {
    // 도메인과 소통 하기 위함
    fun toDomain(): TextResult = TextResult(
        text = text,
        blocks = blocks
    )
}
