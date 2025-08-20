package co.aos.roomdb.di

import android.content.Context
import androidx.room.Room
import co.aos.roomdb.AppDatabase
import co.aos.roomdb.utils.RoomConst
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Room di 모듈
 * */
@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(context,
        AppDatabase::class.java, RoomConst.ROOM_DB_NAME).build()

    /** user 관련 */
    @Provides
    fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDao()

    /** wish 관련 */
    @Provides
    fun provideWishDao(appDatabase: AppDatabase) = appDatabase.wishDao()
}