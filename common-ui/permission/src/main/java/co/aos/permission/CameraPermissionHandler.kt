package co.aos.permission

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * 카메라 권한 체크
 * */
@Composable
fun CameraPermissionHandler(
    onPermission: (isGranted: Boolean) -> Unit
) {
    val context = LocalContext.current

    // 권한 체크 후 처리
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        onPermission(isGranted)
    }

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {
        // 카메라 권한 미 허용 시 처리
        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    } else {
        onPermission.invoke(true)
    }
}