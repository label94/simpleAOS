package co.aos.domain.model

import androidx.annotation.Keep
import java.time.LocalDate

/** 리스트 카드 용도의 diary data model */
@Keep
data class DiarySummary(
    val id: String,
    val title: String,
    val preview: String,
    val tags: List<String>,
    val date: LocalDate
)
