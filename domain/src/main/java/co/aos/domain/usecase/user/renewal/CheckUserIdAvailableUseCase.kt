package co.aos.domain.usecase.user.renewal

/** id 유효성 체크 */
interface CheckUserIdAvailableUseCase {
    suspend operator fun invoke(id: String): Boolean
}