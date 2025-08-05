package com.thehyundai.setting_feature.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thehyundai.setting_feature.state.SettingContract
import com.thehyundai.setting_feature.viewmodel.SettingViewModel

/**
 * 설정 컴포즈 UI
 * */
@Composable
fun SettingScreen(
    settingViewModel: SettingViewModel
) {
    // ui 상태
    val uiState = settingViewModel.uiState.collectAsState()
    val effectFlow = settingViewModel.effect
    val activity = LocalActivity.current

    BackHandler {
        settingViewModel.setEvent(SettingContract.Event.FinishSettingActivity)
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is SettingContract.Effect.FinishActivity -> {
                    // 종료
                    activity?.finish()
                }
            }
        }
    }

    // 컴포즈 UI
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopSettingAppBar(
                onClose = {
                    // 종료
                    settingViewModel.setEvent(SettingContract.Event.FinishSettingActivity)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize().padding(start = 16.dp, end = 16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = if (uiState.value.isNotificationPermissionGranted) "알람 권한 허용" else "알람 권한 미 허용")

                    Switch(
                        checked = uiState.value.isNotificationPermissionGranted,
                        onCheckedChange = {
                            settingViewModel.setEvent(SettingContract.Event.MoveDeviceSetting)
                        }
                    )
                }
            }
        }
    }
}