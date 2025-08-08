package co.aos.domain.usecase.user.impl

import co.aos.domain.model.User
import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.InsertUserUseCase
import javax.inject.Inject

class InsertUserUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : InsertUserUseCase {

    override suspend fun invoke(user: User): Boolean {
        return userRepository.insertUser(user)
    }
}