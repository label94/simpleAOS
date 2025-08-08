package co.aos.domain.usecase.user

import co.aos.domain.model.User

/** 사용자 정보 저장 유스케이스 */
interface InsertUserUseCase {
    suspend operator fun invoke(user: User): Boolean
}