package co.aos.domain.usecase.user.impl

import co.aos.domain.model.User
import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.LoginUseCase
import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : LoginUseCase {
    override suspend fun invoke(id: String, password: String): User? {
        return userRepository.login(id, password)
    }
}