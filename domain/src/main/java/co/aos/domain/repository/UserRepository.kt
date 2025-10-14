package co.aos.domain.repository

import co.aos.domain.model.User

/**
 * User 관련 Repository
 * */
interface UserRepository {
    suspend fun signUp(
        id: String,
        password: String,
        nickName: String,
        localProfileImgCode: Int,
    ): User

    suspend fun login(id: String, password: String): User

    suspend fun getCurrentUser(): User?

    suspend fun signOut()

    suspend fun isNicknameAvailable(nickname: String): Boolean

    suspend fun isIdAvailable(id: String): Boolean

    fun isAutoLogin(): Boolean

    fun setAutoLogin(isAutoLogin: Boolean)

    fun isSaveId(): Boolean

    fun setIsSaveId(isSaveId: Boolean)

    fun getLoginId(): String

    fun setLoginId(loginId: String)
}