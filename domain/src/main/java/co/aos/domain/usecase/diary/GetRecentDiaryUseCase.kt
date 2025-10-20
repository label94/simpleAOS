package co.aos.domain.usecase.diary

import co.aos.domain.model.DiarySummary
import co.aos.domain.model.PagedResult

/** 최근 diary 조회 관련 유스케이스 */
interface GetRecentDiaryUseCase {
    suspend operator fun invoke(uid: String, pageSize: Int, cursor: String?): PagedResult<DiarySummary>
}