package co.aos.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import co.aos.roomdb.dao.UserDao
import co.aos.roomdb.entity.UserEntity

/**
 * App DataBase
 * */
@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    /** 사용자 정보 관련 테이블 */
    abstract fun userDao(): UserDao
}