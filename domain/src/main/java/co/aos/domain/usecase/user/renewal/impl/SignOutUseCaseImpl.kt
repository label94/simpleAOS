package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.SignOutUseCase
import javax.inject.Inject

class SignOutUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): SignOutUseCase {
    override suspend fun invoke() {
        userRepository.signOut()
    }
}