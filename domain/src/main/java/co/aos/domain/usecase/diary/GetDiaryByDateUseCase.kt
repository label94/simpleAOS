package co.aos.domain.usecase.diary

import co.aos.domain.model.DiarySummary
import java.time.LocalDate

/** 날짜 별 조회 관련 유스케이스 */
interface GetDiaryByDateUseCase {
    suspend operator fun invoke(uid: String, day: LocalDate): List<DiarySummary>
}