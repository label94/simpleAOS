package co.aos.user_feature.utils

import co.aos.user_feature.R

/**
 * User feature 에서 사용하는 상수 정의
 * */
object UserConst {

    /** 회원 페이지 관련 상수 정의 */
    enum class UserPage(val id: Int, val pageName: String) {
        JOIN(0, "UserJoin")
    }

    /** 로컬 프로필 용 이미지 */
    enum class UserProfileImage(val code: Int, val resId: Int) {
        PROFILE_FIRST(0, R.drawable.profile_cat),
        PROFILE_SECOND(1, R.drawable.profile_bear),
        PROFILE_THIRD(2, R.drawable.profile_rabbit),
        PROFILE_FOURTH(3, R.drawable.profile_fox),
        PROFILE_FIFTH(4, R.drawable.profile_owl),
        PROFILE_SIXTH(5, R.drawable.profile_dog)
    }
}