package co.aos.user_feature.join.model

import co.aos.domain.model.LegacyUser

/**
 * 회원가입 관련 데이터 모델
 * */
data class JoinUserModel(
    val id: String = "",
    val password: String = "",
    val nickname: String = "",
    val profileImagePath: String? = null
)

fun JoinUserModel.toDomain() = LegacyUser(
    id = id,
    password = password,
    nickname = nickname,
    profileImagePath = profileImagePath
)
