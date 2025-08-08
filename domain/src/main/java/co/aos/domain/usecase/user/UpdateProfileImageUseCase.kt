package co.aos.domain.usecase.user

/** 프로필 이미지 변경 유스케이스 */
interface UpdateProfileImageUseCase {
    suspend operator fun invoke(id: String, profileImagePath: String): Boolean
}