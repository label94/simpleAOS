package co.aos.domain.usecase.diary.impl

import co.aos.domain.model.DiaryEntryUpdate
import co.aos.domain.repository.DiaryRepository
import co.aos.domain.usecase.diary.UpdateDiaryUseCase
import javax.inject.Inject

class UpdateDiaryUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): UpdateDiaryUseCase {
    override suspend fun invoke(
        uid: String,
        entryId: String,
        update: DiaryEntryUpdate
    ) {
        diaryRepository.update(uid, entryId, update)
    }
}