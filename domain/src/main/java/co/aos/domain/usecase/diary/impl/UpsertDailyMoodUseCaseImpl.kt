package co.aos.domain.usecase.diary.impl

import co.aos.domain.repository.DiaryRepository
import co.aos.domain.usecase.diary.UpsertDailyMoodUseCase
import java.time.LocalDate
import javax.inject.Inject

class UpsertDailyMoodUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): UpsertDailyMoodUseCase {
    override suspend fun invoke(uid: String, day: LocalDate, mood: Int) {
        diaryRepository.upsertDailyMood(uid, day, mood)
    }
}