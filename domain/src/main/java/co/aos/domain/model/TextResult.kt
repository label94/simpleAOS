package co.aos.domain.model

/**
 * 인식 관련 데이터 Set
 * */
data class TextResult(
    val text: String,
    val blocks: List<String>
)
