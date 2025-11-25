package co.aos.domain.model

import androidx.annotation.Keep
import java.time.LocalDate

/** diary data model */
@Keep
data class DiaryEntry(
    val id: String? = null,
    val title: String,
    val body: String,
    val tags: List<String>,
    val date: LocalDate,           // 컨텐츠 기준 날짜(작성날짜)
    val updateDate: LocalDate,
    val pinned: Boolean
)
