package co.aos.common

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 스낵바를 표시하는 컴포저블 함수
 *
 * - Toast 메세지 대신 표시하는 Snack bar 를 정의
 *
 * @param snackBarHostState : 스낵바 상태
 * @param coroutineScope : 코루틴 스코프
 * @param message : 메시지
 */
fun showSnackBarMessage(
    snackBarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    message: String
) {
    coroutineScope.launch {
        snackBarHostState.currentSnackbarData?.dismiss()
        snackBarHostState.showSnackbar(message)
    }
}