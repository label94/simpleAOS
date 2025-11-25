package co.aos.domain.model

import androidx.annotation.Keep
import java.time.LocalDate

/** diary 업데이트 용 data model */
@Keep
data class DiaryEntryUpdate(
    val title: String? = null,
    val body: String? = null,
    val tags: List<String>? = null,
    val date: LocalDate? = null,
    val pinned: Boolean? = null
)
