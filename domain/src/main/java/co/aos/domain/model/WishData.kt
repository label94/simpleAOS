package co.aos.domain.model

/**
 * Wish 관련 data set
 * */
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