package co.aos.domain.usecase.user.legacy

import co.aos.domain.model.LegacyUser

/** 사용자 정보 조회 유스케이스 */
interface SearchUserInfoUseCase {
    suspend operator fun invoke(id: String): LegacyUser?
}