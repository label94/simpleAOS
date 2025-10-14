package co.aos.domain.usecase.user.renewal

/** 닉네임 유효성 검증 유스케이스 */
interface CheckNicknameAvailableUseCase {
    suspend operator fun invoke(nickname: String): Boolean
}