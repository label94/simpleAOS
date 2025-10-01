package co.aos.domain.usecase.user.legacy.impl

import co.aos.domain.model.User
import co.aos.domain.repository.LegacyUserRepository
import co.aos.domain.usecase.user.legacy.SearchUserInfoUseCase
import javax.inject.Inject

class SearchUserInfoUseCaseImpl @Inject constructor(
    private val legacyUserRepository: LegacyUserRepository
) : SearchUserInfoUseCase {

    override suspend fun invoke(id: String): User? {
        return legacyUserRepository.getUser(id)
    }
}