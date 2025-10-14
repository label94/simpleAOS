package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.model.User
import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.SignUpUseCase
import javax.inject.Inject

class SignUpUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): SignUpUseCase {
    override suspend fun invoke(
        id: String,
        password: String,
        nickName: String,
        localProfileImgCode: Int
    ): User {
        return userRepository.signUp(id, password, nickName, localProfileImgCode)
    }
}