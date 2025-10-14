package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.model.User
import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.GetCurrentUserUseCase
import javax.inject.Inject

class GetCurrentUserUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): GetCurrentUserUseCase {
    override suspend fun invoke(): User? {
        return userRepository.getCurrentUser()
    }
}