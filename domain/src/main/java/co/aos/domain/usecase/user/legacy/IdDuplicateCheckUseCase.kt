package co.aos.domain.usecase.user.legacy

/** id 중복 체크 관련 유스케이스 */
interface IdDuplicateCheckUseCase {
    suspend operator fun invoke(id: String): Boolean
}