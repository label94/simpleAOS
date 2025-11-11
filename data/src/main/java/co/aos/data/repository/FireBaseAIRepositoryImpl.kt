package co.aos.data.repository

import co.aos.domain.repository.FireBaseAIRepository
import co.aos.firebase.ai.FirebaseAIDataSource
import javax.inject.Inject

class FireBaseAIRepositoryImpl @Inject constructor(
    private val aiDataSource: FirebaseAIDataSource
): FireBaseAIRepository {
    override suspend fun generateDailyPrompts(
        mood: Int?,
        hint: String?
    ): List<String> {
        return aiDataSource.generateDailyPrompts(mood, hint)
    }
}