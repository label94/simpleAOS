package co.aos.data.entity

import co.aos.domain.model.DiaryEntry
import co.aos.domain.model.DiaryListItem
import co.aos.domain.model.DiarySummary
import co.aos.firebase.model.DiaryEntryDto
import java.time.LocalDate
import java.time.ZoneId

/** diary data 관련 mapper */

private fun java.util.Date.toLocalDate(): LocalDate =
    this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

fun Pair<String, DiaryEntryDto>.toSummary(): DiarySummary {
    val (id, dto) = this
    return DiarySummary(
        id = id,
        title = dto.title,
        preview = dto.body.take(120),
        tags = dto.tags,
        date = (dto.date ?: java.util.Date()).toLocalDate(),
    )
}

fun Pair<String, DiaryEntryDto>.toEntry(): DiaryEntry {
    val (id, dto) = this
    return DiaryEntry(
        id = id,
        title = dto.title,
        body = dto.body,
        tags = dto.tags,
        date = (dto.date ?: java.util.Date()).toLocalDate(),
        updateDate = (dto.updatedAt ?: java.util.Date()).toLocalDate(),
        pinned = dto.pinned
    )
}

fun DiaryEntryDto.toListItemOrNull(id: String): DiaryListItem? {
    val d = date ?: return null
    val local = d.toLocalDate()
    val preview = body.lineSequence().firstOrNull()?.take(60)?.trim().orEmpty()
    return DiaryListItem(
        id = id,
        title = title.ifBlank { "제목 없음" },
        bodyPreview = preview,
        tags = tags,
        date = local,
        pinned = pinned
    )
}
