package co.aos.domain.usecase.user.renewal

/**
 * ID 저장 활성화 유무 관련 유스케이스
 * */
interface EnableIsSaveIdUseCase {
    operator fun invoke(isSaveId: Boolean)
}