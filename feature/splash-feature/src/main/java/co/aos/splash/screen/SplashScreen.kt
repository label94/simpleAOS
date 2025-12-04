package co.aos.splash.screen

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.aos.common.APP_MAIN_NAME
import co.aos.permission.NotificationPermissionHandler
import co.aos.splash.state.SplashContract
import co.aos.splash.viewmodel.SplashViewModel

/**
 * 스플래시 화면
 * */
@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNotFirstLaunch: () -> Unit,
    onFirstLaunch: () -> Unit,
    onMoveHomePage: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    // This effect handles the visibility of the system navigation bar.
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as? Activity)?.window
        if (window != null) {
            DisposableEffect(Unit) {
                val insetsController = WindowCompat.getInsetsController(window, view)

                // 해당 화면에서 하단 네비게이션 바 영역 숨김 처리
                insetsController.hide(WindowInsetsCompat.Type.navigationBars())

                // 스와이프 시 하단 네비게이션 바 영역 표시하기 위한 처리
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

                onDispose {
                    // 해당 화면 종료가 되면 다시 하단 네비게이션 바 표시
                    insetsController.show(WindowInsetsCompat.Type.navigationBars())
                }
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = APP_MAIN_NAME,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = White,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.Serif
        )
    }

    // 최초 실행이 아닌 경우에만 해당 화면에서 알림 권한 체크
    if (uiState.isFirstLaunch) {
        NotificationPermissionHandler {}
    }

    // 뒤로가기 핸들러
    val activity = LocalView.current.context as? Activity
    BackHandler {
        if (activity?.isFinishing == false) {
            activity.finish()
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
                is SplashContract.Effect.MoveHomePage -> {
                    onMoveHomePage.invoke()
                }
            }
        }
    }
}
