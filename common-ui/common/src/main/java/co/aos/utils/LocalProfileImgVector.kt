package co.aos.utils

/** 로컬 프로필 이미지 resId 관련 유틸 */
object LocalProfileImgVector {
    /** 로컬 프로필 이미지 restId 반환 */
    fun getLocalProfileImageVector(code: Int): Int {
        return when(code) {
            UserProfileImage.PROFILE_FIRST.code -> {
                UserProfileImage.PROFILE_FIRST.resId
            }
            UserProfileImage.PROFILE_SECOND.code -> {
                UserProfileImage.PROFILE_SECOND.resId
            }
            UserProfileImage.PROFILE_THIRD.code -> {
                UserProfileImage.PROFILE_THIRD.resId
            }
            UserProfileImage.PROFILE_FOURTH.code -> {
                UserProfileImage.PROFILE_FOURTH.resId
            }
            UserProfileImage.PROFILE_FIFTH.code -> {
                UserProfileImage.PROFILE_FIFTH.resId
            }
            UserProfileImage.PROFILE_SIXTH.code -> {
                UserProfileImage.PROFILE_SIXTH.resId
            }
            else -> {
                UserProfileImage.PROFILE_FIRST.resId
            }
        }
    }
}