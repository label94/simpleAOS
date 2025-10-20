package co.aos.domain.usecase.diary.impl

import co.aos.domain.model.DiaryEntry
import co.aos.domain.repository.DiaryRepository
import co.aos.domain.usecase.diary.AddDiaryUseCase
import javax.inject.Inject

class AddDiaryUseCaseImpl @Inject constructor(
    private val diaryRepository: DiaryRepository
): AddDiaryUseCase {
    override suspend fun invoke(
        uid: String,
        entry: DiaryEntry
    ): String {
        return diaryRepository.add(uid, entry)
    }
}