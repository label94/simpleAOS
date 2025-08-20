package co.aos.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import co.aos.roomdb.dao.UserDao
import co.aos.roomdb.dao.WishDao
import co.aos.roomdb.entity.UserEntity
import co.aos.roomdb.entity.WishEntity
import co.aos.roomdb.entity.WishImageEntity

/**
 * App DataBase
 * */
@Database(
    entities = [UserEntity::class, WishEntity::class, WishImageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    /** 사용자 정보 관련 DAO */
    abstract fun userDao(): UserDao

    /** Wish 정보 관련 DAO */
    abstract fun wishDao(): WishDao
}