package co.aos.domain.model

import java.time.LocalDate

/** 리스트 카드 용도의 diary data model */
data class DiarySummary(
    val id: String,
    val title: String,
    val preview: String,
    val mood: Int?,
    val tags: List<String>,
    val date: LocalDate
)
