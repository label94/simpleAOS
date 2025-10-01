package co.aos.domain.usecase.user.legacy.impl

import co.aos.domain.model.User
import co.aos.domain.repository.LegacyUserRepository
import co.aos.domain.usecase.user.legacy.LoginUseCase
import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(
    private val legacyUserRepository: LegacyUserRepository
) : LoginUseCase {
    override suspend fun invoke(id: String, password: String): User? {
        return legacyUserRepository.login(id, password)
    }

    override suspend fun invoke(): User? {
        return legacyUserRepository.autoLogin()
    }
}