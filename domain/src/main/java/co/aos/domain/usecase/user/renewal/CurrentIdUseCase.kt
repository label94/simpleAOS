package co.aos.domain.usecase.user.renewal

/** 현재 로그인 한 id 가져오기 관련 유스케이스 */
interface CurrentIdUseCase {
    suspend operator fun invoke(): String?
}