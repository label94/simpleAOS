package co.aos.domain.usecase.diary

import co.aos.domain.model.DiaryEntry

/** 특정 diary 조회 관련 유스케이스 */
interface GetDiaryUseCase {
    suspend operator fun invoke(uid: String, entryId: String): DiaryEntry?
}