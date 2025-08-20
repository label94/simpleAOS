package co.aos.roomdb.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 위시 데이터를 관리하는 테이블
 * */
@Entity(
    tableName = "wishes",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ownerId")]
)
data class WishEntity(
    /** 게시물 id */
    @PrimaryKey(autoGenerate = true) val wishId: Long = 0,

    /** 작성자(user와 연결) */
    val ownerId: String,

    /** 컨텐츠 제목 */
    val title: String,

    /** 컨텐츠 내용 */
    val content: String,
)
