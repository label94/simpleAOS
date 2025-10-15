package co.aos.domain.usecase.user.renewal

/** 패스워드 변경 관련 유스케이스 */
interface ChangePasswordUseCase {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String
    ): Result<Unit>
}