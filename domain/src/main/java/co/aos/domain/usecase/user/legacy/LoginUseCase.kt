package co.aos.domain.usecase.user.legacy

import co.aos.domain.model.User

/** 로그인 유스케이스 */
interface LoginUseCase {

    /** 로그인 요청 */
    suspend operator fun invoke(id: String, password: String): User?

    /** 자동 로그인 요청 */
    suspend operator fun invoke(): User?
}