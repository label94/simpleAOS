package co.aos.domain.usecase.user.legacy

import co.aos.domain.model.LegacyUser

/** 사용자 정보 저장 유스케이스 */
interface InsertUserUseCase {
    suspend operator fun invoke(legacyUser: LegacyUser): Boolean
}