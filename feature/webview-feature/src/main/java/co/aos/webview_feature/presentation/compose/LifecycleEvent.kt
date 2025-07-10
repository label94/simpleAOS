package co.aos.webview_feature.presentation.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * 컴포저블 함수 내에서 Android lifecycle 체크하기 위한 함수
 *
 * - 컴포즈 Screen UI 내 Android lifecycle 체크 하기 위한 유틸
 *
 * @param onCreate : onCreate 이벤트
 * @param onResume : onResume 이벤트
 * @param onPause : onPause 이벤트
 * @param onDestroy : onDestroy 이벤트
 * */
@Composable
fun LifecycleEventListener(
    onCreate: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onDestroy: () -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // android lifecycle 체크 하여 콜백 함수 형태로 전달
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> onCreate()
                Lifecycle.Event.ON_RESUME -> onResume()
                Lifecycle.Event.ON_PAUSE -> onPause()
                Lifecycle.Event.ON_DESTROY -> onDestroy()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}