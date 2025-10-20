package co.aos.home.main.model

import co.aos.domain.model.DiaryEntry
import co.aos.domain.model.DiarySummary

/** UI 구성에 필요한 데이터 가공 */

data class DiaryCardUi(
    val id: String,
    val title: String,
    val preview: String,
    val moodEmoji: String,
    val tags: List<String>,
    val dateText: String,
    val pinned: Boolean = false
)

private val EMOJI = listOf("😣","😕","🙂","😊","🤩")

fun DiarySummary.toCardUi() = DiaryCardUi(
    id, title.ifBlank { "제목 없음" }, preview,
    EMOJI.getOrElse((mood ?: 3) - 1) { "🙂" },
    tags, date.toString()
)
data class DiaryEntryUi(
    val id: String, val title: String, val body: String,
    val moodEmoji: String, val tags: List<String>, val dateText: String, val pinned: Boolean
)
fun DiaryEntry.toDetailUi() = DiaryEntryUi(
    id ?: "", title.ifBlank { "제목 없음" }, body,
    EMOJI.getOrElse((mood ?: 3) - 1) { "🙂" }, tags, date.toString(), pinned
)
