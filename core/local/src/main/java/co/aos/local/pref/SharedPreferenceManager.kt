package co.aos.local.pref

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

/**
 * 로컬 데이터 관리를 위한 Shared 매니저 클래스
 * */
@Singleton
class SharedPreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private  val sharedPreferences: SharedPreferences
) {

    /**
     * 안드로이드 주요 컴포넌트 외 클래스에 hilt 주입을 사용하기 위해 필요!
     * */
    companion object {
        fun getSharedPreferenceManager(context: Context): SharedPreferenceManager =
            EntryPoints.get(context.applicationContext, SharedPreferenceManagerEntryPoint::class.java).run {
                getSharedPreferencesManager()
            }
    }

    /**
     * hilt 주입 관련 필요 메서드
     * */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SharedPreferenceManagerEntryPoint {
        fun getSharedPreferencesManager(): SharedPreferenceManager
    }

    /** Int 타입 처리 */
    fun setInt(key: String, value: Int) {
        sharedPreferences.edit { putInt(key, value) }
    }
    fun getInt(key: String, defaultValue: Int = -1): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    /** String 타입 처리 */
    fun setString(key: String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }
    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    /** Float 타입 처리 */
    fun setFloat(key: String, value: Float) {
        sharedPreferences.edit { putFloat(key, value) }
    }
    fun getFloat(key: String, defaultValue: Float = -1f): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    /** Long 타입 처리 */
    fun setLong(key: String, value: Long) {
        sharedPreferences.edit { putLong(key, value) }
    }
    fun getLong(key: String, defaultValue: Long = -1): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    /** Boolean 타입 처리 */
    fun setBoolean(key: String, value: Boolean) {
        sharedPreferences.edit { putBoolean(key, value) }
    }
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    /** 데이터 삭제 */
    fun removeKey(key: String) {
        sharedPreferences.edit { remove(key) }
    }
    fun clearAll() {
        sharedPreferences.edit { clear() }
    }
}