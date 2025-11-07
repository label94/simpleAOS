package co.aos.domain.usecase.diary

import co.aos.domain.model.DiaryListItem
import java.time.LocalDate

/**
 * 일자별 리스트 요약
 * - 정렬 추가
 * */
interface GetDiarySortListByDateUseCase {
    suspend operator fun invoke(uid: String, day: LocalDate): List<DiaryListItem>
}