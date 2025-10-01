package co.aos.domain.usecase.user.legacy.impl

import co.aos.domain.repository.LegacyUserRepository
import co.aos.domain.usecase.user.legacy.UpdateNickNameUseCase
import javax.inject.Inject

class UpdateNickNameUseCaseImpl @Inject constructor(
    private val legacyUserRepository: LegacyUserRepository
) : UpdateNickNameUseCase {

    override suspend fun invoke(id: String, nickname: String): Boolean {
        return legacyUserRepository.updateNickName(id, nickname)
    }
}