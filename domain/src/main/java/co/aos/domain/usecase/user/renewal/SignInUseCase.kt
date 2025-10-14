package co.aos.domain.usecase.user.renewal

import co.aos.domain.model.User

/** 로그인 유스케이스 */
interface SignInUseCase {
    suspend operator fun invoke(id: String, password: String): User
}