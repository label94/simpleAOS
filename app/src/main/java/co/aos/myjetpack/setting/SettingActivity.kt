package co.aos.myjetpack.setting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import co.aos.myjetpack.ui.theme.MyJetpackTheme
import co.aos.myutils.log.LogUtil
import com.thehyundai.setting_feature.screen.SettingScreen
import com.thehyundai.setting_feature.state.SettingContract
import com.thehyundai.setting_feature.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * 설정 화면
 * */
@AndroidEntryPoint
class SettingActivity : ComponentActivity() {

    // 설정 뷰모델
    val settingViewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // UI 영역
        setContent {
            MyJetpackTheme {
                SettingScreen(
                    settingViewModel = settingViewModel
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LogUtil.d(LogUtil.DEFAULT_TAG, "Setting Act onResume()")

        // 권한 확인
        settingViewModel.setEvent(SettingContract.Event.CheckNotificationPermission)
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d(LogUtil.DEFAULT_TAG, "Setting Act onDestroy()")
    }
}