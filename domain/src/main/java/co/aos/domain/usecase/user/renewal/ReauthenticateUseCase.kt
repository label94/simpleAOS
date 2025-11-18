package co.aos.domain.usecase.user.renewal

/** 재인증 관련 유스케이스 */
interface ReauthenticateUseCase {
    suspend operator fun invoke(id: String, password: String)
}