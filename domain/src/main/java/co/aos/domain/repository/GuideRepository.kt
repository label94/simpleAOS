package co.aos.domain.repository

/**
 * 접근권한 안내 화면 관련 Repository
 * */
interface GuideRepository {
    /** 최초 실행 유무 값 업데이트 */
    suspend fun updateIsFirstLaunch(isFirstLaunch: Boolean)
}