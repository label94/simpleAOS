package co.aos.domain.repository

import co.aos.domain.model.DiaryEntry
import co.aos.domain.model.DiaryEntryUpdate
import co.aos.domain.model.DiarySummary
import co.aos.domain.model.PagedResult
import java.time.LocalDate

/** diary 관련 repository */
interface DiaryRepository {
    suspend fun add(uid: String, entry: DiaryEntry): String

    suspend fun update(uid: String, entryId: String, update: DiaryEntryUpdate)

    suspend fun delete(uid: String, entryId: String)

    suspend fun getById(uid: String, entryId: String): DiaryEntry?

    suspend fun recent(uid: String, pageSize: Int, cursor: String?): PagedResult<DiarySummary>

    suspend fun byDate(uid: String, day: LocalDate): List<DiarySummary>

    suspend fun search(
        uid: String,
        query: String?,
        from: LocalDate?,
        to: LocalDate?,
        tags: Set<String>,
        pinnedOnly: Boolean,
        pageSize: Int,
        cursor: String?
    ): PagedResult<DiarySummary>

    suspend fun upsertDailyMood(uid: String, day: LocalDate, mood: Int)

    suspend fun weeklyMood(uid: String, endInclusive: LocalDate): List<Int?>

    suspend fun setDiaryPinned(
        uid: String,
        entryId: String,
        pinned: Boolean
    )
}