package co.aos.data.entity

import androidx.annotation.Keep
import co.aos.domain.model.TextResult

/**
 * 인식 관련 Entity data set
 * */
@Keep
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
