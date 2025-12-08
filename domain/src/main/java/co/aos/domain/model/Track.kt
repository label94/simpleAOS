package co.aos.domain.model

/** 미디어 관련 트랙정보 */
data class Track(
    val id: String,
    val title: String,
    val artist: String? = null,
    val url: String,
    val artworkUrl: String? = null
)
