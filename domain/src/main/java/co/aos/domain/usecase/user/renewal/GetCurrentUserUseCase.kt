package co.aos.domain.usecase.user.renewal

import co.aos.domain.model.User

/** 로그인한 사용자 정보 가져오기 */
interface GetCurrentUserUseCase {
    suspend operator fun invoke(): User?
}