package co.aos.data.repository

import co.aos.domain.repository.SplashRepository
import co.aos.local.pref.SharedPreferenceManager
import co.aos.local.pref.consts.SharedConstants
import javax.inject.Inject

/**
 * 스플래시 관련 Repository 구현 클래스
 * */
class SplashRepositoryImpl @Inject constructor(
    private val preferenceManager: SharedPreferenceManager
): SplashRepository {
    override suspend fun isFirstRunApp(): Boolean {
        return preferenceManager.getBoolean(SharedConstants.KEY_IS_FIRST_LAUNCH)
    }
}