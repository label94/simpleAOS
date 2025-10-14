package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.GetLoginIdUseCase
import javax.inject.Inject

class GetLoginIdUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): GetLoginIdUseCase {
    override fun invoke(): String {
        return userRepository.getLoginId()
    }
}