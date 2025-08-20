package co.aos.data.repository

import co.aos.domain.repository.GuideRepository
import co.aos.local.pref.SharedPreferenceManager
import co.aos.local.pref.consts.SharedConstants
import javax.inject.Inject

/**
 * 접근권한 안내 화면 관련 Repository 구현 클래스
 * */
class GuideRepositoryImpl @Inject constructor(
    private val preferenceManager: SharedPreferenceManager
): GuideRepository {
    override suspend fun updateIsFirstLaunch(isFirstLaunch: Boolean) {
        preferenceManager.setBoolean(SharedConstants.KEY_IS_FIRST_LAUNCH, isFirstLaunch)
    }
}