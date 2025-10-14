package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.SetLoginIdUseCase
import javax.inject.Inject

class SetLoginIdUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): SetLoginIdUseCase {
    override fun invoke(loginId: String) {
        userRepository.setLoginId(loginId)
    }
}