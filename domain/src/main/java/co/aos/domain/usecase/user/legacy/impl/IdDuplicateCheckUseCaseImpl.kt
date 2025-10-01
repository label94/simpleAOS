package co.aos.domain.usecase.user.legacy.impl

import co.aos.domain.repository.LegacyUserRepository
import co.aos.domain.usecase.user.legacy.IdDuplicateCheckUseCase
import javax.inject.Inject

/** 중복체크 유스케이스 구현 클래스 */
class IdDuplicateCheckUseCaseImpl @Inject constructor(
    private val legacyUserRepository: LegacyUserRepository
): IdDuplicateCheckUseCase {
    override suspend fun invoke(id: String): Boolean {
       val result = legacyUserRepository.getUser(id)
        return result == null
    }
}