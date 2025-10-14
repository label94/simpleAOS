package co.aos.data.datasource

import co.aos.domain.model.User

/**
 * firebase 연동 관련 dataSource
 * */
interface FirebaseUserDataSource {
    /** 회원가입 */
    suspend fun createUser(id: String, password: String): String

    /** 로그인 */
    suspend fun login(id: String, password: String): String

    /** 현재 로그인의 uid 가져오기 */
    suspend fun currentUid(): String?

    /** 로그아웃 */
    fun signOut()

    /** 사용자 정보 제거 */
    suspend fun deleteCurrentUserIfAny()

    /** 사용자 정보 업데이트 및 등록 */
    suspend fun upsertUser(data: User)

    /** uid에 맞는 user 정보 반환 */
    suspend fun fetchUser(uid: String): User?

    /** 마지막 로그인 시간 업데이트 */
    suspend fun touchLastLogin(uid: String)

    /** 닉네임 유효성 검증 */
    suspend fun isNicknameAvailable(nickname: String): Boolean

    /** 닉네임 예약하기 */
    suspend fun reserveNickname(uid: String, nickname: String)

    /** 닉네임 제거 */
    suspend fun releaseNickname(nickname: String)

    /** id 유효성 체크 */
    suspend fun isIdAvailable(id: String): Boolean

    /** id 컬렉션 생성 */
    suspend fun reserveId(uid: String, id: String)

    /** id 컬렉션 제거 */
    suspend fun releaseId(id: String)
}