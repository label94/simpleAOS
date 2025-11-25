package co.aos.domain.model

import androidx.annotation.Keep

/** 사용자 정보 모델 */
@Keep
data class LegacyUser(
    val id: String,
    val password: String,
    val nickname: String,
    val profileImagePath: String?
)
