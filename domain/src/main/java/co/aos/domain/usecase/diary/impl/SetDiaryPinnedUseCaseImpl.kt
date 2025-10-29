package co.aos.domain.usecase.diary.impl

import co.aos.domain.repository.DiaryRepository
import co.aos.domain.usecase.diary.SetDiaryPinnedUseCase
import javax.inject.Inject

class SetDiaryPinnedUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): SetDiaryPinnedUseCase {
    override suspend fun invoke(
        uid: String,
        entryId: String,
        pinned: Boolean
    ) {
        diaryRepository.setDiaryPinned(uid, entryId, pinned)
    }
}