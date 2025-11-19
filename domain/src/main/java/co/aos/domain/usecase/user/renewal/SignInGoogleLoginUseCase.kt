package co.aos.domain.usecase.user.renewal

import co.aos.domain.model.User

/** 구글 로그인 요청 */
interface SignInGoogleLoginUseCase {
    suspend operator fun invoke(): User?
}