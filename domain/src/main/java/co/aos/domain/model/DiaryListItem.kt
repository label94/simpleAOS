package co.aos.domain.model

import java.time.LocalDate

/**
 * 캘린더/리스트에서 가볍게 보여줄 요약 모델
 */
data class DiaryListItem(
    val id: String,
    val title: String,
    val bodyPreview: String,
    val tags: List<String>,
    val date: LocalDate,
    val pinned: Boolean
)
