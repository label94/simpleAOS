package co.aos.domain.usecase.diary

import co.aos.domain.model.DiaryEntry

/** diary 작성 */
interface AddDiaryUseCase {
    suspend operator fun invoke(uid: String, entry: DiaryEntry): String
}