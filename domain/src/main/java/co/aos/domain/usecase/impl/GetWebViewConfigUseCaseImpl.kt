package co.aos.domain.usecase.impl

import co.aos.domain.model.WebViewConfig
import co.aos.domain.repository.WebViewConfigRepository
import co.aos.domain.usecase.GetWebViewConfigUseCase
import javax.inject.Inject

/**
 * 웹뷰 설정 관련 유스케이스(구현체)
 * */
class GetWebViewConfigUseCaseImpl @Inject constructor(
    private val repository: WebViewConfigRepository
): GetWebViewConfigUseCase {
    override suspend fun invoke(): WebViewConfig {
        return repository.getWebViewConfig()
    }
}