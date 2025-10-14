package co.aos.domain.usecase.user.renewal

/** 로그인 id 반환 관련 유스케이스 */
interface GetLoginIdUseCase {
    operator fun invoke(): String
}