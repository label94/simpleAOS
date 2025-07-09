package co.aos.permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * 알람 권한 관련 체크
 * */
@Composable
fun NotificationPermissionHandler(
    onPermission: (isGranted: Boolean) -> Unit
) {
    val context = LocalContext.current

    // 권한 체크 후 처리
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        onPermission(isGranted)
    }

    // 권한 체크
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Android 13 이상인 경우에만 권한 체크
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            LaunchedEffect(Unit) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // 권한 허용
            onPermission(true)
        }
    } else {
        // 그 외 권한을 체크 하지 않음
        onPermission(true)
    }
}