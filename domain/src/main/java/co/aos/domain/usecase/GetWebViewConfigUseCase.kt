package co.aos.domain.usecase

import co.aos.domain.model.WebViewConfig

/**
 * 웹뷰 설정 관련 유스케이스
 * */
interface GetWebViewConfigUseCase {
    suspend operator fun invoke(): WebViewConfig
}