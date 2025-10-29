package co.aos.domain.usecase.diary.impl

import co.aos.domain.model.DiarySummary
import co.aos.domain.model.PagedResult
import co.aos.domain.repository.DiaryRepository
import co.aos.domain.usecase.diary.SearchDiaryUseCase
import java.time.LocalDate
import javax.inject.Inject

class SearchDiaryUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): SearchDiaryUseCase {
    override suspend fun invoke(
        uid: String,
        query: String?,
        from: LocalDate?,
        to: LocalDate?,
        tags: Set<String>,
        pinnedOnly: Boolean,
        pageSize: Int,
        cursor: String?
    ): PagedResult<DiarySummary> {
        return diaryRepository.search(uid, query, from, to, tags, pinnedOnly, pageSize, cursor)
    }
}