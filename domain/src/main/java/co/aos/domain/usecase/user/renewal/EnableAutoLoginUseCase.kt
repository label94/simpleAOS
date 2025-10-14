package co.aos.domain.usecase.user.renewal

/** 자동 로그인 설정 관련 유스케이스 */
interface EnableAutoLoginUseCase {
    operator fun invoke(isAutoLogin: Boolean)
}