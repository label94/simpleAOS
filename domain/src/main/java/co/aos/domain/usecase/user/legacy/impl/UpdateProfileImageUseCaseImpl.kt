package co.aos.domain.usecase.user.legacy.impl

import co.aos.domain.repository.LegacyUserRepository
import co.aos.domain.usecase.user.legacy.UpdateProfileImageUseCase
import javax.inject.Inject

class UpdateProfileImageUseCaseImpl @Inject constructor(
    private val legacyUserRepository: LegacyUserRepository
) : UpdateProfileImageUseCase {

    override suspend fun invoke(
        id: String,
        profileImagePath: String
    ): Boolean {
        return legacyUserRepository.updateProfileImagePath(id, profileImagePath)
    }
}