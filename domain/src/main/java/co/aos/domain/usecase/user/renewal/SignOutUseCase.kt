package co.aos.domain.usecase.user.renewal

/** 로그아웃 유스케이스 */
interface SignOutUseCase {
    suspend operator fun invoke()
}