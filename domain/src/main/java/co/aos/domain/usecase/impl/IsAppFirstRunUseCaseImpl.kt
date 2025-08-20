package co.aos.domain.usecase.impl

import co.aos.domain.repository.SplashRepository
import co.aos.domain.usecase.IsAppFirstRunUseCase
import javax.inject.Inject

/**
 * 앱 최초 실행 유무 확인 유스케이스 구현 클래스
 * */
class IsAppFirstRunUseCaseImpl @Inject constructor(
    private val splashRepository: SplashRepository
): IsAppFirstRunUseCase {
    override suspend fun invoke(): Boolean {
        return splashRepository.isFirstRunApp()
    }
}