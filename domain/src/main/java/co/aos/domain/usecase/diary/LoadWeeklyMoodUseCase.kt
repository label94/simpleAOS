package co.aos.domain.usecase.diary

import java.time.LocalDate

/** 주간 무드 로드 (7개 docId whereIn) */
interface LoadWeeklyMoodUseCase {
    suspend operator fun invoke(uid: String, endInclusive: LocalDate): List<Int?>
}