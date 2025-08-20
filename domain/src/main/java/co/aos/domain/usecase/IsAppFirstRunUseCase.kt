package co.aos.domain.usecase

/**
 * 앱 최초 실행 유무 확인 유스케이스
 * */
interface IsAppFirstRunUseCase {
    suspend operator fun invoke(): Boolean
}