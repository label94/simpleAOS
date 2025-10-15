package co.aos.domain.usecase.user.renewal

/** 프로필 정보 업데이트 관련 유스케이스 */
interface UpdateMyProfileUseCase {
    suspend operator fun invoke(
        newNick: String,
        newLocalProfileImgCode: Int
    ): Result<Unit>
}