package co.aos.domain.model

import androidx.annotation.Keep

/**
 * 웹뷰 설정 관련 Data set
 * */
@Keep
data class WebViewConfig(
    val userAgent: String
)
