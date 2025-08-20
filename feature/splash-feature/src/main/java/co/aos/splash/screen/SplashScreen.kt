package co.aos.splash.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.aos.permission.NotificationPermissionHandler
import co.aos.splash.state.SplashContract
import co.aos.splash.viewmodel.SplashViewModel
import co.aos.ui.theme.DarkSeaGreen

/**
 * 스플래시 화면
 * */
@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNotFirstLaunch: () -> Unit,
    onFirstLaunch: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect
    val activity = LocalActivity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSeaGreen)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = "Android",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.White,
        )
    }

    // 최초 실행이 아닌 경우에만 해당 화면에서 알림 권한 체크
    if (uiState.isFirstLaunch) {
        NotificationPermissionHandler {}
    }

    // 뒤로가기 핸들러
    BackHandler {
        if (activity?.isFinishing != true) {
            activity?.finish()
        }
    }

    // 1회성 이벤트 처리
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is SplashContract.Effect.MoveLoginPage -> {
                    onNotFirstLaunch.invoke()
                }
                is SplashContract.Effect.MoveGuidePage -> {
                    onFirstLaunch.invoke()
                }
            }
        }
    }
}