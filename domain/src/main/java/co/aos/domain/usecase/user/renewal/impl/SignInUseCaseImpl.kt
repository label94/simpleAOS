package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.model.User
import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.SignInUseCase
import javax.inject.Inject

class SignInUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): SignInUseCase {
    override suspend fun invoke(
        id: String,
        password: String
    ): User {
        return userRepository.login(id, password)
    }
}