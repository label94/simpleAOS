package co.aos.domain.usecase.diary.impl

import co.aos.domain.model.DiaryListItem
import co.aos.domain.repository.DiaryRepository
import co.aos.domain.usecase.diary.GetDiarySortListByMonthUseCase
import java.time.YearMonth
import javax.inject.Inject

class GetDiarySortListByMonthUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): GetDiarySortListByMonthUseCase {
    override suspend fun invoke(
        uid: String,
        ym: YearMonth
    ): List<DiaryListItem> {
        return diaryRepository.listByMonth(uid, ym)
    }
}