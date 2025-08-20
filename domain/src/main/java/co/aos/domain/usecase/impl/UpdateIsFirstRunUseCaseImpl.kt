package co.aos.domain.usecase.impl

import co.aos.domain.repository.GuideRepository
import co.aos.domain.usecase.UpdateIsFirstRunUseCase
import javax.inject.Inject

/**
 * 앱 최초 실행 후 해당 값을 업데이트 하기 위한 유스케이스
 * */
class UpdateIsFirstRunUseCaseImpl @Inject constructor(
    private val guideRepository: GuideRepository
): UpdateIsFirstRunUseCase {
    override suspend fun invoke(isFirstRun: Boolean) {
        guideRepository.updateIsFirstLaunch(isFirstRun)
    }
}