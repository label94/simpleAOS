package co.aos.domain.usecase.diary

/** diary 삭제 관련 유스케이스 */
interface DeleteDiaryUseCase {
    suspend operator fun invoke(uid: String, entryId: String)
}