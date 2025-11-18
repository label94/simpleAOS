package co.aos.myutils.common

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import co.aos.myutils.log.LogUtil

/** 앱 정보 관련 유틸 */
object AppInfoUtils {

    /**
     * 앱의 버전 이름을 가져옵니다.
     * @param context
     * @return Version Name (e.g., "1.0.0")
     */
    fun getAppVersionName(context: Context): String {
        return getPackageInfo(context)?.versionName ?: "N/A"
    }

    /**
     * 앱의 버전 코드를 가져옵니다.
     * @param context
     * @return Version Code (e.g., 1)
     */
    fun getAppVersionCode(context: Context): String {
        val packageInfo = getPackageInfo(context) ?: return "0"
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toString()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(LogUtil.DEFAULT_TAG, "Error getting version code string: $e")
            "0"
        }
    }

    private fun getPackageInfo(context: Context): PackageInfo? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}
