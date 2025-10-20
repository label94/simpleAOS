package co.aos.domain.usecase.diary.impl

import co.aos.domain.model.DiarySummary
import co.aos.domain.model.PagedResult
import co.aos.domain.repository.DiaryRepository
import co.aos.domain.usecase.diary.GetRecentDiaryUseCase
import javax.inject.Inject

class GetRecentDiaryUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): GetRecentDiaryUseCase {
    override suspend fun invoke(
        uid: String,
        pageSize: Int,
        cursor: String?
    ): PagedResult<DiarySummary> {
        return diaryRepository.recent(uid, pageSize, cursor)
    }
}