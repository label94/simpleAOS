package co.aos.domain.usecase.user.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.UpdatePasswordUseCase
import javax.inject.Inject

class UpdatePasswordUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : UpdatePasswordUseCase {
    override suspend fun invoke(id: String, password: String): Boolean {
        return userRepository.updatePassword(id, password)
    }
}