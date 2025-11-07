package co.aos.domain.usecase.diary.impl

import co.aos.domain.model.DiaryListItem
import co.aos.domain.repository.DiaryRepository
import co.aos.domain.usecase.diary.GetDiarySortListByDateUseCase
import java.time.LocalDate
import javax.inject.Inject

class GetDiarySortListByDateUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): GetDiarySortListByDateUseCase {
    override suspend fun invoke(
        uid: String,
        day: LocalDate
    ): List<DiaryListItem> {
        return diaryRepository.listByDate(uid, day)
    }
}