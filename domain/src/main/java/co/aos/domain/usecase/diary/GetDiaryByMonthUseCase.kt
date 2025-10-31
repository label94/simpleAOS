package co.aos.domain.usecase.diary

import co.aos.domain.model.DiarySummary
import java.time.LocalDate

/**
 * 임의의 날짜가 속한 '그 달'의 일기 조회 (현재 날짜 포함 용도로 사용) 관련 유스케이스
 */
interface GetDiaryByMonthUseCase {
    suspend operator fun invoke(uid: String, dayInMonth: LocalDate): List<DiarySummary>
}