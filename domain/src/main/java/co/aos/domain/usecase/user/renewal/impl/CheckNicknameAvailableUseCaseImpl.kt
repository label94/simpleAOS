package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.CheckNicknameAvailableUseCase
import javax.inject.Inject

class CheckNicknameAvailableUseCaseImpl @Inject constructor(
    private val repository: UserRepository
) : CheckNicknameAvailableUseCase {
    override suspend fun invoke(nickname: String): Boolean {
        return repository.isNicknameAvailable(nickname)
    }
}