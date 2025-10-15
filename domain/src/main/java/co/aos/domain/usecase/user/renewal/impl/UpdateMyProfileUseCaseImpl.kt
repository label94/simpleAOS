package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.UpdateMyProfileUseCase
import javax.inject.Inject

class UpdateMyProfileUseCaseImpl @Inject constructor(
    private val repository: UserRepository
): UpdateMyProfileUseCase {
    override suspend fun invoke(
        newNick: String,
        newLocalProfileImgCode: Int
    ): Result<Unit> = repository.updateMyProfile(newNick.trim(), newLocalProfileImgCode)
}