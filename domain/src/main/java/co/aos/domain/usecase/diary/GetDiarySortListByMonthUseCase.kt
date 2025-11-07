package co.aos.domain.usecase.diary

import co.aos.domain.model.DiaryListItem
import java.time.YearMonth

/**
 * 월별 리스트 요약
 * - 정렬 추가
 * */
interface GetDiarySortListByMonthUseCase {
    suspend operator fun invoke(uid: String, ym: YearMonth): List<DiaryListItem>
}