package co.aos.domain.repository

import co.aos.domain.model.WebViewConfig

/**
 * 웹뷰 설정 관련 Repository
 * */
interface WebViewConfigRepository {
    suspend fun getWebViewConfig(): WebViewConfig
}