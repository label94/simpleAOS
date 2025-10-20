package co.aos.domain.model

import java.time.LocalDate

/** diary data model */
data class DiaryEntry(
    val id: String? = null,
    val title: String,
    val body: String,
    val mood: Int?,                // 1..5 or null
    val tags: List<String>,
    val date: LocalDate,           // 컨텐츠 기준 날짜
    val pinned: Boolean
)
