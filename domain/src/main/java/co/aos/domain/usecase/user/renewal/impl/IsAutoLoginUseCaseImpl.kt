package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.IsAutoLoginUseCase
import javax.inject.Inject

class IsAutoLoginUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): IsAutoLoginUseCase {
    override fun invoke(): Boolean {
        return userRepository.isAutoLogin()
    }
}