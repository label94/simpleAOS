package co.aos.domain.usecase.user.legacy.impl

import co.aos.domain.repository.LegacyUserRepository
import co.aos.domain.usecase.user.legacy.UpdatePasswordUseCase
import javax.inject.Inject

class UpdatePasswordUseCaseImpl @Inject constructor(
    private val legacyUserRepository: LegacyUserRepository
) : UpdatePasswordUseCase {
    override suspend fun invoke(id: String, password: String): Boolean {
        return legacyUserRepository.updatePassword(id, password)
    }
}