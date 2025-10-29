package co.aos.domain.usecase.diary

/** 핀 토글 업데이트 관련 유스케이스 */
interface SetDiaryPinnedUseCase {
    suspend operator fun invoke(uid: String, entryId: String, pinned: Boolean)
}