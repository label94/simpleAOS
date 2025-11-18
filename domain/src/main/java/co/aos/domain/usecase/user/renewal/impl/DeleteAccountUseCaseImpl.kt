package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.DeleteAccountUseCase
import javax.inject.Inject

class DeleteAccountUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): DeleteAccountUseCase {
    override suspend fun invoke(currentPassword: String) {
        userRepository.deleteAllUserData(currentPassword)
    }
}