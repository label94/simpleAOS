package co.aos.data.repository

import co.aos.data.datasource.FirebaseUserDataSource
import co.aos.domain.model.User
import co.aos.domain.repository.UserRepository
import co.aos.local.pref.SharedPreferenceManager
import co.aos.local.pref.consts.SharedConstants
import javax.inject.Inject

/**
 * UserRepository 구현 클래스
 * */
class UserRepositoryImpl @Inject constructor(
    private val remote: FirebaseUserDataSource,
    private val preferenceManager: SharedPreferenceManager
): UserRepository {

    override suspend fun signUp(
        id: String,
        password: String,
        nickName: String,
        localProfileImgCode: Int
    ): User {
        val uid = remote.createUser(id, password)

        try {
            // 닉네임 원자적 예약 -> 실패 시 계정 롤백
            remote.reserveNickname(uid, nickName)

            // id 원자적 예약 -> 실패 시 롤백
            remote.reserveId(uid, id)

            val user = User(uid, id, nickName, localProfileImgCode)
            remote.upsertUser(user)
            remote.touchLastLogin(uid)

            return user
        } catch (e: Throwable) {
            // 롤백: 닉네임 해제
            runCatching { remote.releaseNickname(nickName) }

            // 롤백 : id 해제
            runCatching { remote.releaseId(id) }

            // 계정 삭제
            runCatching { remote.deleteCurrentUserIfAny() }
            throw e
        }
    }

    override suspend fun login(
        id: String,
        password: String
    ): User {
        val uid = remote.login(id, password)
        remote.touchLastLogin(uid)
        val user = remote.fetchUser(uid) ?: User(
            uid = uid,
            id = id,
            nickName = "",
            localProfileImgCode = 0
        )
        return user
    }

    override suspend fun getCurrentUser(): User? =
        remote.currentUid()?.let { remote.fetchUser(it) }

    override suspend fun signOut() {
        remote.signOut()

        // 로컬에 저장 된 로그인 관련 preference 값을 전부 초기화
        preferenceManager.setString(SharedConstants.KEY_LOGIN_ID, "")
        preferenceManager.setBoolean(SharedConstants.KEY_IS_AUTO_LOGIN, false)
        preferenceManager.setBoolean(SharedConstants.KEY_SAVE_ID, false)
    }

    override suspend fun isNicknameAvailable(nickname: String): Boolean {
        return remote.isNicknameAvailable(nickname)
    }

    override suspend fun isIdAvailable(id: String): Boolean {
        return remote.isIdAvailable(id)
    }

    override fun isAutoLogin(): Boolean {
        return preferenceManager.getBoolean(SharedConstants.KEY_IS_AUTO_LOGIN, false)
    }

    override fun setAutoLogin(isAutoLogin: Boolean) {
        preferenceManager.setBoolean(SharedConstants.KEY_IS_AUTO_LOGIN, isAutoLogin)
    }

    override fun isSaveId(): Boolean {
        return preferenceManager.getBoolean(SharedConstants.KEY_SAVE_ID, false)
    }

    override fun setIsSaveId(isSaveId: Boolean) {
        preferenceManager.setBoolean(SharedConstants.KEY_SAVE_ID, isSaveId)
    }

    override fun getLoginId(): String {
        return preferenceManager.getString(SharedConstants.KEY_LOGIN_ID, "")
    }

    override fun setLoginId(loginId: String) {
        preferenceManager.setString(SharedConstants.KEY_LOGIN_ID, loginId)
    }

    override suspend fun updateMyProfile(
        newNick: String,
        newLocalProfileImgCode: Int
    ): Result<Unit> = runCatching {
        val uid = remote.currentUid() ?: throw IllegalStateException("NOT_SIGNED_IN")
        remote.updateProfileTransaction(uid, newNick, newLocalProfileImgCode)
    }

    override suspend fun currentId(): String? = remote.currentId()

    override suspend fun updatePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> = runCatching {
        val id = remote.currentId() ?: throw IllegalStateException("ID_NOT_FOUND")
        remote.reauthenticate(id, currentPassword)
        remote.updatePassword(newPassword)
    }

    override suspend fun deleteAllUserData(
        currentPassword: String
    ) {
        val uid = remote.currentUid() ?: throw IllegalStateException("ID_NOT_FOUND")
        val email = remote.currentId() ?: throw IllegalStateException("EMAIL_NOT_FOUND")

        // 1. 현재 uid / email 확인
        val user = remote.fetchUser(uid) ?: throw IllegalStateException("USER_NOT_FOUND")

        // 2. 닉네임, id 정보를 읽고 lowercase 변형
        val nickName = user.nickName.trim().lowercase().ifBlank { null }
        val id = user.id.trim().lowercase().ifBlank { null }

        // 3. 재인증
        remote.reauthenticate(email, currentPassword)

        // 4. Firestore에서 현재 user와 맵핑되는 컬렉션 제거
        remote.deleteAllUserData(uid, id, nickName)

        // 5. FirebaseAuth 사용자 삭제
        remote.deleteCurrentUserIfAny()
    }

    override suspend fun reauthenticate(id: String, password: String) {
        remote.reauthenticate(id, password)
    }

    override suspend fun signWithGoogle(): User? {
        return remote.signInWithGoogle()
    }
}