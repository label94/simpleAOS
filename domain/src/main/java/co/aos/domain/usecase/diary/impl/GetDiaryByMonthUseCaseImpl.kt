package co.aos.domain.usecase.diary.impl

import co.aos.domain.model.DiarySummary
import co.aos.domain.repository.DiaryRepository
import co.aos.domain.usecase.diary.GetDiaryByMonthUseCase
import java.time.LocalDate
import javax.inject.Inject

class GetDiaryByMonthUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): GetDiaryByMonthUseCase {
    override suspend fun invoke(
        uid: String,
        dayInMonth: LocalDate
    ): List<DiarySummary> {
        return diaryRepository.entriesByMonth(uid, dayInMonth)
    }
}