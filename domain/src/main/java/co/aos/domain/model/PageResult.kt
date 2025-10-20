package co.aos.domain.model

/** page 관련 data model */
data class PagedResult<T>(val items: List<T>, val nextCursor: String?)
