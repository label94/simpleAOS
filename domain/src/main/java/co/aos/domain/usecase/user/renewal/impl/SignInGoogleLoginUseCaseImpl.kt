package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.model.User
import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.SignInGoogleLoginUseCase
import javax.inject.Inject

class SignInGoogleLoginUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): SignInGoogleLoginUseCase {
    override suspend fun invoke(): User? {
        return userRepository.signWithGoogle()
    }
}