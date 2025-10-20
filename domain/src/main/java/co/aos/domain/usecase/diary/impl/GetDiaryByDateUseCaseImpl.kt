package co.aos.domain.usecase.diary.impl

import co.aos.domain.model.DiarySummary
import co.aos.domain.repository.DiaryRepository
import co.aos.domain.usecase.diary.GetDiaryByDateUseCase
import java.time.LocalDate
import javax.inject.Inject

class GetDiaryByDateUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): GetDiaryByDateUseCase {
    override suspend fun invoke(
        uid: String,
        day: LocalDate
    ): List<DiarySummary> {
        return diaryRepository.byDate(uid, day)
    }
}