package co.aos.roomdb.relation

import androidx.room.Embedded
import androidx.room.Relation
import co.aos.roomdb.entity.WishEntity
import co.aos.roomdb.entity.WishImageEntity

/**
 * Wish, 이미지 목록까지 가져오기 위한 Relation
 * */
data class WishWithImages(
    @Embedded val wish: WishEntity,
    @Relation(
        parentColumn = "wishId",
        entityColumn = "wishOwnerId"
    )
    val images: List<WishImageEntity>
)
