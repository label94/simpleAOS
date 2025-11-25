package co.aos.domain.model

import androidx.annotation.Keep

/**
 * 인식 관련 데이터 Set
 * */
@Keep
data class TextResult(
    val text: String,
    val blocks: List<String>
)
