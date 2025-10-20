package co.aos.domain.usecase.diary.impl

import co.aos.domain.repository.DiaryRepository
import co.aos.domain.usecase.diary.DeleteDiaryUseCase
import javax.inject.Inject

class DeleteDiaryUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): DeleteDiaryUseCase {
    override suspend fun invoke(uid: String, entryId: String) {
        diaryRepository.delete(uid, entryId)
    }
}