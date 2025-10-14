package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.IsSaveIdUseCase
import javax.inject.Inject

class IsSaveIdUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): IsSaveIdUseCase {
    override fun invoke(): Boolean {
        return userRepository.isSaveId()
    }
}