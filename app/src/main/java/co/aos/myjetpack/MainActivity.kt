package co.aos.myjetpack

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import co.aos.base.BaseActivity
import co.aos.myjetpack.intro.newintro.AppNavHost
import co.aos.myutils.log.LogUtil
import co.aos.network_error_feature.viewmodel.NetworkStatusViewModel
import co.aos.ui.theme.MyJetpackTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/**
 * Main
 * */
@AndroidEntryPoint
class MainActivity : BaseActivity() {

    // 네트워크 상태 관련 뷰모델
    private val networkStatusViewModel: NetworkStatusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        LogUtil.d(LogUtil.DEFAULT_TAG, "onCreate")

        setContent {
            MyJetpackTheme {
                AppNavHost()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        LogUtil.d(LogUtil.DEFAULT_TAG, "onNewIntent : ${intent.toString()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d(LogUtil.DEFAULT_TAG, "onDestroy")
    }
}