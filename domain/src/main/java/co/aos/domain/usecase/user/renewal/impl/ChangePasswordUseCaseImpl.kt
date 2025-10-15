package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.ChangePasswordUseCase
import javax.inject.Inject

class ChangePasswordUseCaseImpl @Inject constructor(
    private val repository: UserRepository
): ChangePasswordUseCase {
    override suspend fun invoke(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        require(newPassword.length >=6) {"비밀번호는 6자 이상이어야 합니다."}
        return repository.updatePassword(currentPassword, newPassword)
    }
}