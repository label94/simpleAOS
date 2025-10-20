package co.aos.domain.usecase.diary.impl

import co.aos.domain.repository.DiaryRepository
import co.aos.domain.usecase.diary.LoadWeeklyMoodUseCase
import java.time.LocalDate
import javax.inject.Inject

class LoadWeeklyMoodUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): LoadWeeklyMoodUseCase {
    override suspend fun invoke(
        uid: String,
        endInclusive: LocalDate
    ): List<Int?> {
        return diaryRepository.weeklyMood(uid, endInclusive)
    }
}