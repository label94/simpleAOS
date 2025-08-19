package co.aos.domain.usecase.user.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.IdDuplicateCheckUseCase
import javax.inject.Inject

/** 중복체크 유스케이스 구현 클래스 */
class IdDuplicateCheckUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): IdDuplicateCheckUseCase {
    override suspend fun invoke(id: String): Boolean {
       val result = userRepository.getUser(id)
        return result == null
    }
}