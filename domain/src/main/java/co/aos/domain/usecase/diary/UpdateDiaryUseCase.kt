package co.aos.domain.usecase.diary

import co.aos.domain.model.DiaryEntryUpdate

/** diary 관련 수정 유스케이스 */
interface UpdateDiaryUseCase {
    suspend operator fun invoke(uid: String, entryId: String, update: DiaryEntryUpdate)
}