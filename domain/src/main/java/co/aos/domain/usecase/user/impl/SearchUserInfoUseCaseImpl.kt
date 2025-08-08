package co.aos.domain.usecase.user.impl

import co.aos.domain.model.User
import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.SearchUserInfoUseCase
import javax.inject.Inject

class SearchUserInfoUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : SearchUserInfoUseCase {

    override suspend fun invoke(id: String): User? {
        return userRepository.getUser(id)
    }
}