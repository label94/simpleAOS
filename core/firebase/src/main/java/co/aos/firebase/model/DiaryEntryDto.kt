package co.aos.firebase.model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/** 다이어리 관련 컨텐츠 Entry */
@IgnoreExtraProperties
data class DiaryEntryDto(
    val title: String = "",
    val body: String = "",
    val tags: List<String> = emptyList(),
    val pinned: Boolean = false,
    @ServerTimestamp val date: Date? = null,       // 컨텐츠 기준 날짜(0시 고정 권장) - 최초 작성 날짜
    @ServerTimestamp val updatedAt: Date? = null   // 업데이트 한 날짜
)
