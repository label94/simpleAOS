package co.aos.domain.usecase.ai

/** 다이어리 작성에 도움을 주는 오늘의 영감 관련 유스케이스 */
interface GetDailyPromptsUseCase {
    suspend operator fun invoke(
        mood: Int?,
        hint: String?
    ): List<String>
}