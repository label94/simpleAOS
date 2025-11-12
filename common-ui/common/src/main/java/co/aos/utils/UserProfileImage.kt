package co.aos.utils

import co.aos.common.R

/** 로컬 프로필 용 이미지 */
enum class UserProfileImage(val code: Int, val resId: Int) {
    PROFILE_FIRST(0, R.drawable.profile_cat),
    PROFILE_SECOND(1, R.drawable.profile_bear),
    PROFILE_THIRD(2, R.drawable.profile_rabbit),
    PROFILE_FOURTH(3, R.drawable.profile_fox),
    PROFILE_FIFTH(4, R.drawable.profile_owl),
    PROFILE_SIXTH(5, R.drawable.profile_dog)
}