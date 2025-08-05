package co.aos.base.utils

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.net.toUri

/**
 * 외부 브라우저 연동
 * */
fun Context.openBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}

/** 액티비티 실행 */
fun Context.moveActivity(callActivityClass: Class<*>, key: String, url: String) {
    val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
    val intent = Intent(this, callActivityClass).apply {
        putExtra(key, url)

        // 기존 스택 유지하면서, 대상 액티비티를 앞으로 이동 시킴
        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
    }
    this.startActivity(intent, options.toBundle())
}

/** 기기 설정화면으로 이동 */
fun Context.openDeviceSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .setData("package:${this.packageName}".toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    if (intent.resolveActivity(this.packageManager) != null) {
        this.startActivity(intent)
    }
}

/** 액티비티 실행 */
fun Context.moveActivity(callActivityClass: Class<*>) {
    val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
    val intent = Intent(this, callActivityClass).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}