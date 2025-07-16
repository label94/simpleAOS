package co.aos.base.utils

import android.content.Context
import android.content.Intent
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