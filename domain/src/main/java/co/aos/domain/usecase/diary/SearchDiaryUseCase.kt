package co.aos.domain.usecase.diary

import co.aos.domain.model.DiarySummary
import co.aos.domain.model.PagedResult
import java.time.LocalDate

/** 간이 검색(서버 범위: 날짜/핀, 나머지(태그/텍스트)는 클라 필터) */
interface SearchDiaryUseCase {
    suspend operator fun invoke(
        uid: String,
        query: String?,
        from: LocalDate?,
        to: LocalDate?,
        tags: Set<String>,
        pinnedOnly: Boolean,
        pageSize: Int,
        cursor: String?
    ): PagedResult<DiarySummary>
}