package co.aos.data.repository

import co.aos.data.entity.toDomain
import co.aos.data.entity.toEntity
import co.aos.domain.model.User
import co.aos.domain.repository.LegacyUserRepository
import co.aos.local.pref.SharedPreferenceManager
import co.aos.local.pref.consts.SharedConstants
import co.aos.myutils.log.LogUtil
import co.aos.roomdb.dao.UserDao
import javax.inject.Inject

/**
 * 사용자 관련 처리를 위한 Repository 구현 클래스
 * */
class LegacyUserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val preferenceManager: SharedPreferenceManager
) : LegacyUserRepository {
    override suspend fun insertUser(userEntity: User): Boolean {
        return userDao.insertUser(userEntity.toEntity()) != -1L
    }

    override suspend fun login(
        id: String,
        password: String
    ): User? {
        val result = userDao.login(id, password)

        // DB에서 로그인 데이터가 존재하면, 임시로 SharedPreference 에 저장
        result?.let {
            preferenceManager.setString(SharedConstants.KEY_LOGIN_ID, id)
            preferenceManager.setString(SharedConstants.KEY_LOGIN_PWD, password)
        }

        return result?.toDomain()
    }

    override suspend fun updateProfileImagePath(
        id: String,
        profileImagePath: String
    ): Boolean {
        return userDao.updateProfileImagePath(id, profileImagePath) > 0
    }

    override suspend fun getUser(id: String): User? {
        val result = userDao.getUser(id)
        return result?.toDomain()
    }

    override suspend fun updateNickName(
        id: String,
        nickname: String
    ): Boolean {
        return userDao.updateNickname(id, nickname) > 0
    }

    override suspend fun updatePassword(
        id: String,
        password: String
    ): Boolean {
        return userDao.updatePassword(id, password) > 0
    }

    override suspend fun deleteUser(id: String): Boolean {
        return userDao.deleteUser(id) > 0
    }

    override suspend fun isAutoLogin(): Boolean {
        return preferenceManager.getBoolean(SharedConstants.KEY_IS_AUTO_LOGIN)
    }

    override suspend fun setAutoLogin(isAutoLogin: Boolean) {
        preferenceManager.setBoolean(SharedConstants.KEY_IS_AUTO_LOGIN, isAutoLogin)
    }

    override suspend fun autoLogin(): User? {
        val id = preferenceManager.getString(SharedConstants.KEY_LOGIN_ID)
        val pwd = preferenceManager.getString(SharedConstants.KEY_LOGIN_PWD)

        if (id.isNotEmpty() && pwd.isNotEmpty()) {
            LogUtil.d(LogUtil.LOGIN_LOG_TAG, "Auto Login!!")
            val result = userDao.login(id, pwd)
            return result?.toDomain()
        }
        return null
    }
}