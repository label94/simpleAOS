package co.aos.data.entity

import androidx.annotation.Keep
import co.aos.domain.model.WebViewConfig

/**
 * 웹뷰 셋팅에 필요한 데이터 set
 *
 * @param userAgent 웹뷰에 설정한 UserAgent
 * */
@Keep
data class WebViewConfigEntity(
    val userAgent: String
) {
    // 도메인과 통신하기 위함
    fun toDomain(): WebViewConfig = WebViewConfig(
        userAgent = userAgent
    )
}
