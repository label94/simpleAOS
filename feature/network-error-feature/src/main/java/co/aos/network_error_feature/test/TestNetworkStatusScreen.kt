package co.aos.network_error_feature.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.aos.myutils.log.LogUtil
import co.aos.network_error_feature.screen.NetworkErrorScreen
import co.aos.network_error_feature.state.NetworkStatusContract
import co.aos.network_error_feature.viewmodel.NetworkStatusViewModel
import kotlinx.coroutines.launch

/**
 * 테스트 용도의 네트워크 상태 확인 UI
 * */
@Composable
fun TestNetworkStatusScreen(
    networkStatusViewModel: NetworkStatusViewModel = hiltViewModel()
) {
    // 네트워크 UI 상태
    val networkUiState = networkStatusViewModel.uiState.collectAsState()

    // 공통 effect 처리
    EffectHandler(
        networkStatusViewModel = networkStatusViewModel
    )

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "컨텐츠 영역"
            )
        }
    }

    // 네트워크 오류 UI
    if (networkUiState.value.isShowErrorScreen) {
        NetworkErrorScreen(
            onRetry = {
                networkStatusViewModel.setEvent(NetworkStatusContract.Event.ReloadWebView)
            }
        )
    }
}

/**
 * Launched Effect 처리를 위한 공통 Handler
 * */
@Composable
private fun EffectHandler(
    networkStatusViewModel: NetworkStatusViewModel
) {
    LaunchedEffect(Unit) {
        // 네트워크 상태 effect 감지
        launch {
            networkStatusViewModel.effect.collect { effect ->
                when(effect) {
                    is NetworkStatusContract.Effect.Retry -> {
                        LogUtil.e("TestLog", "LaunchedEffect Retry!")
                    }
                }
            }
        }
    }
}