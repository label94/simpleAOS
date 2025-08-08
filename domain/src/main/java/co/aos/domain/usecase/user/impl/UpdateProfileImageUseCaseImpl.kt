package co.aos.domain.usecase.user.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.UpdateProfileImageUseCase
import javax.inject.Inject

class UpdateProfileImageUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : UpdateProfileImageUseCase {

    override suspend fun invoke(
        id: String,
        profileImagePath: String
    ): Boolean {
        return userRepository.updateProfileImagePath(id, profileImagePath)
    }
}