package co.aos.domain.usecase.user.legacy

/** 사용자 정보 삭제 유스케이스 */
interface DeleteUserUseCase {
    suspend operator fun invoke(id: String): Boolean
}