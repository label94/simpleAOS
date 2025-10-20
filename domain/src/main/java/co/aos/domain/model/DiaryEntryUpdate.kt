package co.aos.domain.model

import java.time.LocalDate

/** diary 업데이트 용 data model */
data class DiaryEntryUpdate(
    val title: String? = null,
    val body: String? = null,
    val mood: Int? = null,
    val tags: List<String>? = null,
    val date: LocalDate? = null,
    val pinned: Boolean? = null
)
