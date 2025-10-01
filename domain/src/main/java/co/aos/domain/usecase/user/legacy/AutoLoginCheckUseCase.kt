package co.aos.domain.usecase.user.legacy

/** 자동 로그인 활성화 유무 체크 유스케이스 */
interface AutoLoginCheckUseCase {
    /** 자동 로그인 유무 체크 */
    suspend operator fun invoke(): Boolean

    /** 자동 로그인 활성화 유무 설정 */
    suspend operator fun invoke(isAutoLogin: Boolean)
}