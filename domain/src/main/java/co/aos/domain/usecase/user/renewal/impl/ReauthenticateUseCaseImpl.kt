package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.ReauthenticateUseCase
import javax.inject.Inject

class ReauthenticateUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): ReauthenticateUseCase {
    override suspend fun invoke(id: String, password: String) {
        userRepository.reauthenticate(id, password)
    }
}