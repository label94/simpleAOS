package co.aos.data.datasource.impl

import co.aos.data.datasource.FirebaseUserDataSource
import co.aos.domain.model.User
import co.aos.firebase.auth.FirebaseAuthDataSource
import co.aos.firebase.firestore.FirebaseFirestoreDataSource
import co.aos.firebase.model.UserDto
import javax.inject.Inject
import androidx.core.net.toUri

/**
 * Firebase User 연동 관련 DataSource
 * */
class FirebaseUserDataSourceImpl @Inject constructor(
    private val authDS: FirebaseAuthDataSource,
    private val fsDS: FirebaseFirestoreDataSource
): FirebaseUserDataSource {
    override suspend fun createUser(id: String, password: String): String =
        authDS.signUp(id, password)

    override suspend fun login(id: String, password: String): String =
        authDS.signIn(id, password)

    override suspend fun currentUid(): String? = authDS.currentUid()

    override fun signOut() {
        authDS.signOut()
    }

    override suspend fun deleteCurrentUserIfAny() {
        authDS.deleteCurrentUserIfAny()
    }

    override suspend fun upsertUser(data: User) {
        val dto = UserDto(
            uid = data.uid,
            id = data.id,
            nickName = data.nickName,
            localProfileImgCode = data.localProfileImgCode
        )
        fsDS.upsertUserTransaction(dto)
    }

    override suspend fun fetchUser(uid: String): User? {
        val dto = fsDS.fetchUser(uid) ?: return null
        return User(
            uid = dto.uid,
            id = dto.id,
            nickName = dto.nickName,
            localProfileImgCode = dto.localProfileImgCode
        )
    }

    override suspend fun touchLastLogin(uid: String) {
        fsDS.updateLastLogin(uid)
    }

    override suspend fun isNicknameAvailable(nickname: String) =
        fsDS.isNicknameAvailable(nickname.lowercase())

    override suspend fun reserveNickname(uid: String, nickname: String) {
        fsDS.reserveNickName(uid, nickname.lowercase())
    }

    override suspend fun releaseNickname(nickname: String) {
        fsDS.releaseNickName(nickname.lowercase())
    }

    override suspend fun isIdAvailable(id: String): Boolean =
        fsDS.isIdAvailable(id)

    override suspend fun reserveId(uid: String, id: String) {
        fsDS.reserveId(uid, id)
    }

    override suspend fun releaseId(id: String) {
        fsDS.releaseId(id)
    }

    override suspend fun updateProfileTransaction(
        uid: String,
        newNick: String,
        newLocalProfileImgCode: Int
    ) {
        fsDS.updateProfileTransaction(uid, newNick, newLocalProfileImgCode)
    }

    override suspend fun currentId(): String? {
        return authDS.currentId()
    }

    override suspend fun reauthenticate(id: String, password: String) {
        authDS.reauthenticate(id, password)
    }

    override suspend fun updatePassword(newPassword: String) {
        authDS.updatePassword(newPassword)
    }
}