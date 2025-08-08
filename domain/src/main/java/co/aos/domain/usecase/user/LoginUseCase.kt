package co.aos.domain.usecase.user

import co.aos.domain.model.User

/** 로그인 유스케이스 */
interface LoginUseCase {
    suspend operator fun invoke(id: String, password: String): User?
}