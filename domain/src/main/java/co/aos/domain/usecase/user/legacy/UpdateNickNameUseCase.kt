package co.aos.domain.usecase.user.legacy

/** 닉네임 변경 유스케이스 */
interface UpdateNickNameUseCase {
    suspend operator fun invoke(id: String, nickname: String): Boolean
}