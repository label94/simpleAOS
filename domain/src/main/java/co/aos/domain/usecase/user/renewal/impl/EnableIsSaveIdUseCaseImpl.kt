package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.EnableIsSaveIdUseCase
import javax.inject.Inject

class EnableIsSaveIdUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): EnableIsSaveIdUseCase {
    override fun invoke(isSaveId: Boolean) {
        userRepository.setIsSaveId(isSaveId)
    }
}