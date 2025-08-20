package co.aos.domain.usecase

/**
 * 앱 최초 실행 후 해당 값을 업데이트 하기 위한 유스케이스
 * */
interface UpdateIsFirstRunUseCase {
    suspend operator fun invoke(isFirstRun: Boolean)
}