package co.aos.domain.usecase.user.renewal

/** 로그인 id 저장 관련 유스케이스 */
interface SetLoginIdUseCase {
    operator fun invoke(loginId: String)
}