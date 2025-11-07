package co.aos.data.repository

import co.aos.data.datasource.FirebaseUserDataSource
import co.aos.data.entity.toEntry
import co.aos.data.entity.toListItemOrNull
import co.aos.data.entity.toSummary
import co.aos.domain.model.DiaryEntry
import co.aos.domain.model.DiaryEntryUpdate
import co.aos.domain.model.DiaryListItem
import co.aos.domain.model.DiarySummary
import co.aos.domain.model.PagedResult
import co.aos.domain.repository.DiaryRepository
import co.aos.firebase.FirebaseFireStoreKey
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val fs: FirebaseUserDataSource
): DiaryRepository {
    override suspend fun add(
        uid: String,
        entry: DiaryEntry
    ): String =
        fs.addDiaryEntry(uid, entry.title, entry.body, entry.tags, entry.date, entry.pinned)

    override suspend fun update(
        uid: String,
        entryId: String,
        update: DiaryEntryUpdate
    ) {
        val map = mapOf(
            FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_TITLE.key to update.title,
            FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_BODY.key to update.body,
            FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_TAGS.key to update.tags,
            FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_DATE.key to update.date,
            FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_PINNED.key to update.pinned
        )
        fs.updateDiaryEntry(uid, entryId, map)
    }

    override suspend fun delete(uid: String, entryId: String) {
        fs.deleteDiaryEntry(uid, entryId)
    }

    override suspend fun getById(
        uid: String,
        entryId: String
    ): DiaryEntry? =
        fs.getDiaryEntry(uid, entryId)?.let { entryId to it }?.toEntry()

    override suspend fun recent(
        uid: String,
        pageSize: Int,
        cursor: String?
    ): PagedResult<DiarySummary> {
        val (list, next) = fs.recentDiaryEntries(uid, pageSize, cursor)
        return PagedResult(list.map { it.toSummary() }, next)
    }

    override suspend fun byDate(
        uid: String,
        day: LocalDate
    ): List<DiarySummary> {
        return fs.entriesByDate(uid, day).map { it.toSummary() }
    }

    override suspend fun search(
        uid: String,
        query: String?,
        from: LocalDate?,
        to: LocalDate?,
        tags: Set<String>,
        pinnedOnly: Boolean,
        pageSize: Int,
        cursor: String?
    ): PagedResult<DiarySummary> {
        val (serverList, next) = fs.searchDiaryEntries(uid, from, to, pinnedOnly, pageSize, cursor)

        // 클라 필터
        var list = serverList.map { it.toSummary() }
        if (tags.isNotEmpty()) {
            list = list.filter { it.tags.any { t -> t in tags } }
        }
        if (!query.isNullOrBlank()) {
            val ql = query.lowercase()
            list = list.filter { it.title.lowercase().contains(ql) || it.preview.lowercase().contains(ql) }
        }
        return PagedResult(list, next)
    }

    override suspend fun upsertDailyMood(
        uid: String,
        day: LocalDate,
        mood: Int
    ) {
        fs.upsertDailyMood(uid, day, mood)
    }

    override suspend fun weeklyMood(
        uid: String,
        endInclusive: LocalDate
    ): List<Int?> {
        val map = fs.loadWeeklyMood(uid, endInclusive)
        val days = (0..6).map { endInclusive.minusDays( (6 - it).toLong() ) }
        return days.map { map[it.toString()] }
    }

    override suspend fun setDiaryPinned(
        uid: String,
        entryId: String,
        pinned: Boolean
    ) {
        fs.setDiaryPinned(uid, entryId, pinned)
    }

    override suspend fun entriesByMonth(
        uid: String,
        yearMonth: YearMonth
    ): List<DiarySummary> {
        return fs.entriesByMonth(uid, yearMonth).map { it.toSummary() }
    }

    override suspend fun entriesByMonth(
        uid: String,
        dayInMonth: LocalDate
    ): List<DiarySummary> {
        return fs.entriesByMonth(uid, dayInMonth).map { it.toSummary() }
    }

    override suspend fun listByMonth(
        uid: String,
        yearMonth: YearMonth
    ): List<DiaryListItem> {
        val pairs = fs.entriesByMonth(uid, yearMonth)
        return pairs.mapNotNull { (id, dto) -> dto.toListItemOrNull(id) }
            .sortedByDescending { it.date }
    }

    override suspend fun listByDate(
        uid: String,
        day: LocalDate
    ): List<DiaryListItem> {
        val pairs = fs.entriesByDate(uid, day)
        return pairs.mapNotNull { (id, dto) -> dto.toListItemOrNull(id) }
            .sortedByDescending { it.date }
    }
}