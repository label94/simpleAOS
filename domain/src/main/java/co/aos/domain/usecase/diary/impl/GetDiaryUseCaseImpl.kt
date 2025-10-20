package co.aos.domain.usecase.diary.impl

import co.aos.domain.model.DiaryEntry
import co.aos.domain.repository.DiaryRepository
import co.aos.domain.usecase.diary.GetDiaryUseCase
import javax.inject.Inject

class GetDiaryUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): GetDiaryUseCase {
    override suspend fun invoke(
        uid: String,
        entryId: String
    ): DiaryEntry? {
        return diaryRepository.getById(uid, entryId)
    }
}