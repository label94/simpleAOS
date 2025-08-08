package co.aos.roomdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 사용자 정보를 저장하는 테이블
 * */
@Entity(tableName = "user")
data class UserEntity(
    /** id */
    @PrimaryKey val id: String,

    /** 패스워드 */
    val password: String,

    /** 닉네임 */
    val nickname: String,

    /** 프로필 이미지 경로 */
    val profileImagePath: String?
)
