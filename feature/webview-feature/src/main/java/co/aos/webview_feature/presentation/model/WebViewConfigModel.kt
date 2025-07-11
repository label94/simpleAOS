package co.aos.webview_feature.presentation.model

import co.aos.domain.model.WebViewConfig

/**
 * Presentaion 영역에서 사용할 웹뷰 설정 관련 모델
 * */
data class WebViewConfigModel(
    val url: String,
    val userAgent: String
)

fun WebViewConfig.toPresentation(): WebViewConfigModel =
    WebViewConfigModel(
        url = url,
        userAgent = userAgent
    )
