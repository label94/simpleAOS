package co.aos.domain.usecase.user.renewal

/** 자동 로그인 설정 값 가져오기 관련 유스케이스 */
interface IsAutoLoginUseCase {
    operator fun invoke(): Boolean
}