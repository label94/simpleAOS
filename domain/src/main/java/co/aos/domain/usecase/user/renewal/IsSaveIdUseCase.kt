package co.aos.domain.usecase.user.renewal

/** ID 저장 유무 값을 가져오는 유스케이스 */
interface IsSaveIdUseCase {
    operator fun invoke(): Boolean
}