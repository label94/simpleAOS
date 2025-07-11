package co.aos.data.entity

import co.aos.domain.model.WebViewConfig

/**
 * 웹뷰 셋팅에 필요한 데이터 set
 *
 * @param userAgent 웹뷰에 설정한 UserAgent
 * */
data class WebViewConfigEntity(
    val url: String,
    val userAgent: String
) {
    // 도메인과 통신하기 위함
    fun toDomain(): WebViewConfig = WebViewConfig(
        url = url,
        userAgent = userAgent
    )
}
