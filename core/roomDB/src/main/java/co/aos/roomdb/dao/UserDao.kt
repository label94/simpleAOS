package co.aos.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.aos.roomdb.entity.UserEntity

/**
 * 사용자 관련 DB 연산
 * */
@Dao
interface UserDao {
    /** 사용자 정보 저장 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userEntity: UserEntity): Long

    /** 로그인 */
    @Query("SELECT * FROM user WHERE id = :id AND password = :password")
    suspend fun login(id: String, password: String) : UserEntity?

    /** 프로필 이미지 업데이트 */
    @Query("UPDATE user SET profileImagePath = :profileImagePath WHERE id = :id")
    suspend fun updateProfileImagePath(id: String, profileImagePath: String) : Int

    /** 사용자 정보 조회 */
    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getUser(id: String) : UserEntity?

    /** 사용자 닉네임 업데이트 */
    @Query("UPDATE user SET nickname = :nickname WHERE id = :id")
    suspend fun updateNickname(id: String, nickname: String) : Int

    /** 사용자 패스워드 업데이트 */
    @Query("UPDATE user SET password = :password WHERE id = :id")
    suspend fun updatePassword(id: String, password: String) : Int

    /** 사용자 정보 삭제 */
    @Query("DELETE FROM user WHERE id = :id")
    suspend fun deleteUser(id: String) : Int
}