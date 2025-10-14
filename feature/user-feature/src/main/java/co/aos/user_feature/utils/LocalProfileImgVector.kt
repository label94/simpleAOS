package co.aos.user_feature.utils

/** 로컬 프로필 이미지 resId 관련 유틸 */
object LocalProfileImgVector {
    /** 로컬 프로필 이미지 restId 반환 */
    fun getLocalProfileImageVector(code: Int): Int {
        return when(code) {
            UserConst.UserProfileImage.PROFILE_FIRST.code -> {
                UserConst.UserProfileImage.PROFILE_FIRST.resId
            }
            UserConst.UserProfileImage.PROFILE_SECOND.code -> {
                UserConst.UserProfileImage.PROFILE_SECOND.resId
            }
            UserConst.UserProfileImage.PROFILE_THIRD.code -> {
                UserConst.UserProfileImage.PROFILE_THIRD.resId
            }
            UserConst.UserProfileImage.PROFILE_FOURTH.code -> {
                UserConst.UserProfileImage.PROFILE_FOURTH.resId
            }
            UserConst.UserProfileImage.PROFILE_FIFTH.code -> {
                UserConst.UserProfileImage.PROFILE_FIFTH.resId
            }
            UserConst.UserProfileImage.PROFILE_SIXTH.code -> {
                UserConst.UserProfileImage.PROFILE_SIXTH.resId
            }
            else -> {
                UserConst.UserProfileImage.PROFILE_FIRST.resId
            }
        }
    }
}