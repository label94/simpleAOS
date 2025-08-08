package co.aos.domain.usecase.user.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.UpdateNickNameUseCase
import javax.inject.Inject

class UpdateNickNameUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : UpdateNickNameUseCase {

    override suspend fun invoke(id: String, nickname: String): Boolean {
        return userRepository.updateNickName(id, nickname)
    }
}