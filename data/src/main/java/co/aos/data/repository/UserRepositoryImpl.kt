package co.aos.data.repository

import co.aos.data.datasource.FirebaseUserDataSource
import co.aos.domain.model.User
import co.aos.domain.repository.UserRepository
import javax.inject.Inject

/**
 * UserRepository 구현 클래스
 * */
class UserRepositoryImpl @Inject constructor(
    private val remote: FirebaseUserDataSource
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
    }

    override suspend fun isNicknameAvailable(nickname: String): Boolean {
        return remote.isNicknameAvailable(nickname)
    }

    override suspend fun isIdAvailable(id: String): Boolean {
        return remote.isIdAvailable(id)
    }
}