package co.aos.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import co.aos.roomdb.entity.WishEntity
import co.aos.roomdb.entity.WishImageEntity
import co.aos.roomdb.relation.WishWithImages

/**
 * 위시 리스트 관련 DB 연산
 * */
@Dao
interface WishDao {
    @Transaction
    @Query("SELECT * FROM wishes WHERE ownerId = :id")
    suspend fun getWishesByUser(id: String): List<WishWithImages>

    @Insert
    suspend fun insertWish(wish: WishEntity): Long

    @Insert
    suspend fun insertImage(images: List<WishImageEntity>)
}