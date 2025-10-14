package co.aos.domain.usecase.user.renewal

import co.aos.domain.model.User

/** 회원가입 유스케이스 */
interface SignUpUseCase {
    suspend operator fun invoke(
        id: String,
        password: String,
        nickName: String,
        localProfileImgCode: Int,
    ): User
}