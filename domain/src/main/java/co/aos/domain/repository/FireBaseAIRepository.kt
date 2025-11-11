package co.aos.domain.repository

/**
 * Firebase AI Logic 관련 Repository
 * */
interface FireBaseAIRepository {

    /** 영감 관련 프롬프트 */
    suspend fun generateDailyPrompts(
        mood: Int?,
        hint: String?
    ): List<String>
}