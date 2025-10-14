package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.EnableAutoLoginUseCase
import javax.inject.Inject

class EnableAutoLoginUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): EnableAutoLoginUseCase {
    override fun invoke(isAutoLogin: Boolean) {
        userRepository.setAutoLogin(isAutoLogin)
    }
}