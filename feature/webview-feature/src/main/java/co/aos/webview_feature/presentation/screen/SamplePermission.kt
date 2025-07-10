package co.aos.webview_feature.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import co.aos.permission.BlueToothPermissionHandler
import co.aos.permission.MediaPermissionHandler
import co.aos.permission.NotificationPermissionHandler

/**
 * 권한 관련 샘플 UI
 * */
@Composable
fun SamplePermissionScreen() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val context = LocalContext.current

        val notificationCheck = remember { mutableStateOf(false) }
        val mediaCheck = remember { mutableStateOf(false) }
        val bluetoothCheck = remember { mutableStateOf(false) }

        Button(onClick = {
            if (!notificationCheck.value) {
                notificationCheck.value = true
            }
        }) {
            Text(text = "알림 권한 요청")
        }

        Button(onClick = {
            if (!mediaCheck.value) {
                mediaCheck.value = true
            }
        }) {
            Text(text = "미디어 권한 요청")
        }

        Button(onClick = {
            if (!bluetoothCheck.value) {
                bluetoothCheck.value = true
            }
        }) {
            Text(text = "블루투스 권한 요청")
        }

        // 알림 권한 관련 샘플 코드
        if (notificationCheck.value) {
            NotificationPermissionHandler { isGranted ->
                if (isGranted) {
                    Toast.makeText(context, "알림 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 미디어 권한 관련 샘플 코드
        if (mediaCheck.value) {
            MediaPermissionHandler(isVideo = false) {}
        }

        // 블루투스 권한 관련 샘플 코드
        if (bluetoothCheck.value) {
            BlueToothPermissionHandler {  }
        }
    }
}