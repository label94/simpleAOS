package co.aos.firebase.model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/** firebase 내 User Dto */
@IgnoreExtraProperties
data class UserDto(
    val uid: String = "", // 회원 등록 시 발급 되는 uid
    val id: String = "", // 로그인 id(email)
    val nickName: String = "", // 닉네임
    val localProfileImgCode: Int = 0, // 로컬 용 프로필 이미지 코드
    @ServerTimestamp val createdAt: Date? = null,
    @ServerTimestamp val updatedAt: Date? = null,
    @ServerTimestamp val lastLoginAt: Date? = null
)
