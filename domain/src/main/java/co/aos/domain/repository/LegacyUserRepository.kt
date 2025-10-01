package co.aos.domain.repository

import co.aos.domain.model.LegacyUser

/**
 * 사용자 관련 처리를 위한 Repository
 * */
interface LegacyUserRepository {
    /** 사용자 정보 저장 */
    suspend fun insertUser(legacyUserEntity: LegacyUser) : Boolean

    /** 로그인 */
    suspend fun login(id: String, password: String): LegacyUser?

    /** 프로필 이미지 업데이트 */
    suspend fun updateProfileImagePath(id: String, profileImagePath: String) : Boolean

    /** 사용자 정보 가져오기 */
    suspend fun getUser(id: String): LegacyUser?

    /** 닉네임 업데이트 */
    suspend fun updateNickName(id: String, nickname: String) : Boolean

    /** 비밀번호 업데이트 */
    suspend fun updatePassword(id: String, password: String) : Boolean

    /** 사용자 정보 삭제 */
    suspend fun deleteUser(id: String) : Boolean

    /** 자동 로그인 활성화 유무 */
    suspend fun  isAutoLogin() : Boolean

    /** 자동 로그인 활성화 및 비활성화*/
    suspend fun setAutoLogin(isAutoLogin: Boolean)

    /** 자동 로그인 요청 */
    suspend fun autoLogin() : LegacyUser?
}