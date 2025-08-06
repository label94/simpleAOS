package co.aos.myjetpack.intro.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import co.aos.myjetpack.intro.state.IntroContract
import co.aos.myjetpack.intro.viewmodel.IntroViewModel
import co.aos.myjetpack.web.screen.MainWebViewScreen
import co.aos.network_error_feature.viewmodel.NetworkStatusViewModel
import co.aos.permission.NotificationPermissionHandler
import co.aos.webview_renewal_feature.viewmodel.WebViewModel

/**
 * Intro 컴포즈 UI
 * */
@Composable
fun IntroScreen(
    introViewModel: IntroViewModel,
    webViewModel: WebViewModel,
    networkStatusViewModel: NetworkStatusViewModel
) {
    val activity = LocalActivity.current

    // 상태 관련
    val introUiState = introViewModel.uiState.collectAsState()
    val effectFlow = introViewModel.effect
    val requestNotificationPermission = remember { mutableStateOf(false) }

    // 백키 핸들러
    BackHandler {
        introViewModel.setEvent(IntroContract.Event.OnBackPressed)
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        // 메인 웹뷰 스크린
        MainWebViewScreen(
            networkStatusViewModel = networkStatusViewModel,
            webViewModel = webViewModel
        )

        // 스플래시 스크린
        AnimatedVisibility(
            visible = introUiState.value.isShowSplash,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(delayMillis = 500))
        ) {
            SplashScreen(
                onBack = {
                    // 백키 이벤트 호출
                    introViewModel.setEvent(IntroContract.Event.OnBackPressed)
                }
            )
        }

        // 접근 권한 안내 스크린
        AnimatedVisibility(
            visible = introUiState.value.isFirstLaunch,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(delayMillis = 500))
        ) {
            GuideScreen(
                onComplete = {
                    // 다음 단계 실행
                    introViewModel.setEvent(IntroContract.Event.OnNextStep)
                },
                onBack = {
                    // 백키 이벤트 호출
                    introViewModel.setEvent(IntroContract.Event.OnBackPressed)
                },
                requiredPermissionList = introUiState.value.guideRequiredPermissionList,
                optionalPermissionList = introUiState.value.guideOptionalPermissionList
            )
        }
    }

    // effect 처리
    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when(effect) {
                is IntroContract.Effect.FinishActivity -> {
                    if (activity?.isFinishing == false) {
                        activity.finish() // 액티비티 종료
                    }
                }
                is IntroContract.Effect.CheckNotificationPermission -> {
                    requestNotificationPermission.value = true // 알림 권한 체크
                }
            }
        }
    }

    // 알림 권한 체크
    if (requestNotificationPermission.value) {
        NotificationPermissionHandler { isGranted ->
            introViewModel.setEvent(IntroContract.Event.UpdateNotificationPermission(isGranted))
        }
    }
}