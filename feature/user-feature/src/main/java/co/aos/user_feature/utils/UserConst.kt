package co.aos.user_feature.utils

/**
 * User feature 에서 사용하는 상수 정의
 * */
object UserConst {

    /** 회원 페이지 관련 상수 정의 */
    enum class UserPage(val id: Int, val pageName: String) {
        JOIN(0, "UserJoin")
    }
}