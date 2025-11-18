package co.aos.domain.usecase.user.renewal

/** 회원탈퇴 유스케이스 */
interface DeleteAccountUseCase {
    suspend operator fun invoke(currentPassword: String)
}