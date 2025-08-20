package co.aos.roomdb.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Wish Image 관련 테이블
 * */
@Entity(
    tableName = "wish_images",
    foreignKeys = [
        ForeignKey(
            entity = WishEntity::class,
            parentColumns = ["wishId"],
            childColumns = ["wishOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("wishOwnerId")]
)
data class WishImageEntity(
    @PrimaryKey(autoGenerate = true) val imageId: Long = 0,
    val wishOwnerId: Long,
    val imageUrl: String
)