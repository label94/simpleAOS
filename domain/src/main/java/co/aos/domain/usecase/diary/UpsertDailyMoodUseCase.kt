package co.aos.domain.usecase.diary

import java.time.LocalDate

interface UpsertDailyMoodUseCase {
    suspend operator fun invoke(uid: String, day: LocalDate, mood: Int)
}