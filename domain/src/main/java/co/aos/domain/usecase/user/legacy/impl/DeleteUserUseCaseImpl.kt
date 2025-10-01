package co.aos.domain.usecase.user.legacy.impl

import co.aos.domain.repository.LegacyUserRepository
import co.aos.domain.usecase.user.legacy.DeleteUserUseCase
import javax.inject.Inject

class DeleteUserUseCaseImpl @Inject constructor(
    private val legacyUserRepository: LegacyUserRepository
) : DeleteUserUseCase {
    override suspend fun invoke(id: String): Boolean {
        return legacyUserRepository.deleteUser(id)
    }
}