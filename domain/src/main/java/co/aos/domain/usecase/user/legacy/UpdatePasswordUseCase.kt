package co.aos.domain.usecase.user.legacy

/** 패스워드 변경 유스케이스 */
interface UpdatePasswordUseCase {
    suspend operator fun invoke(id: String, password: String): Boolean
}