package co.aos.domain.usecase.user.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.DeleteUserUseCase
import javax.inject.Inject

class DeleteUserUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : DeleteUserUseCase {
    override suspend fun invoke(id: String): Boolean {
        return userRepository.deleteUser(id)
    }
}