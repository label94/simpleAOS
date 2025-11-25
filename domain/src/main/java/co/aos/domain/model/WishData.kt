package co.aos.domain.model

import androidx.annotation.Keep

/**
 * Wish 관련 data set
 * */
@Keep
data class Wish(
    val wishId: Long,
    val ownerId: String,
    val title: String,
    val content: String,
    val images: List<WishImage> = emptyList()
)

data class WishImage(
    val imageId: Long,
    val wishOwnerId: Long,
    val imageUrl: String
)