package co.aos.domain.model

/** 사용자 정보 모델 */
data class User(
    val id: String,
    val password: String,
    val nickname: String,
    val profileImagePath: String?
)
