package co.aos.domain.repository

import co.aos.domain.model.User

/**
 * 스플래시 관련 Repository
 * */
interface SplashRepository {
    /** 최초 앱 실행 유무 */
    suspend fun isFirstRunApp(): Boolean
}