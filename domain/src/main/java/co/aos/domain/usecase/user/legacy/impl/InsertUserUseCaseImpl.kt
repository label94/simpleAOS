package co.aos.domain.usecase.user.legacy.impl

import co.aos.domain.model.User
import co.aos.domain.repository.LegacyUserRepository
import co.aos.domain.usecase.user.legacy.InsertUserUseCase
import javax.inject.Inject

class InsertUserUseCaseImpl @Inject constructor(
    private val legacyUserRepository: LegacyUserRepository
) : InsertUserUseCase {

    override suspend fun invoke(user: User): Boolean {
        return legacyUserRepository.insertUser(user)
    }
}