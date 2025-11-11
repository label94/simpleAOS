package co.aos.domain.usecase.ai.impl

import co.aos.domain.repository.FireBaseAIRepository
import co.aos.domain.usecase.ai.GetDailyPromptsUseCase
import javax.inject.Inject

class GetDailyPromptsUseCaseImpl @Inject constructor(
    private val repository: FireBaseAIRepository
): GetDailyPromptsUseCase {
    override suspend fun invoke(
        mood: Int?,
        hint: String?
    ): List<String> {
        return repository.generateDailyPrompts(mood, hint)
    }
}