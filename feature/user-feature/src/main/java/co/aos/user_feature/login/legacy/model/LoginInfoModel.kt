package co.aos.user_feature.login.legacy.model

/**
 * 로그인 했을 때의 정보 데이터
 * */
data class LoginInfoModel(
    val id: String = "",
    val password: String = "",
    val nickname: String = "",
    val profileImagePath: String? = null
)