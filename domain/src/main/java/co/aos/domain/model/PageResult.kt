package co.aos.domain.model

import androidx.annotation.Keep

/** page 관련 data model */
@Keep
data class PagedResult<T>(val items: List<T>, val nextCursor: String?)
