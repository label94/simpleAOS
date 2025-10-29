package co.aos.home.detail.model

import java.time.Instant
import java.time.LocalDate

/** 상세 화면에서 쓰는 다이어리 모델 */
data class DiaryDetail(
    val id: String,            // 문서 ID
    val title: String,
    val body: String,
    val tags: List<String>,
    val date: LocalDate,       // 일기 날짜(로컬 의미, 저장은 UTC 자정 Date)
    val pinned: Boolean,
)