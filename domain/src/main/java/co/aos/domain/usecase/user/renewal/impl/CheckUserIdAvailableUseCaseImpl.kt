package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.CheckUserIdAvailableUseCase
import javax.inject.Inject

class CheckUserIdAvailableUseCaseImpl @Inject constructor(
    private val repository: UserRepository
): CheckUserIdAvailableUseCase {
    override suspend fun invoke(id: String): Boolean {
        return repository.isIdAvailable(id)
    }
}