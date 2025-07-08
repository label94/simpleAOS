package co.aos.permission

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * 미디어 관련 권한 체크
 * */
@Composable
fun MediaPermissionHandler(
    isVideo: Boolean,
    onPermission: () -> Unit
) {
    val context = LocalContext.current

    // 권한 요청 처리
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissionsResult ->
        val allGranted = permissionsResult.all { it.value }

        if (allGranted) {
            // 권한 허용
            handleGrantedPermission(context, onPermission)
        } else {
            // 설정 메뉴로 이동 팝업 표시
            showPermissionDialog(context, isVideo)
        }
    }

    // os 별 분기 처리
    val permissions = remember(isVideo) {
        val commonPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableListOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA)
        } else {
            mutableListOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }

        // 동영상 관련
        if (isVideo) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                commonPermissions += Manifest.permission.READ_MEDIA_VIDEO
            }
            commonPermissions += Manifest.permission.RECORD_AUDIO
        }
        commonPermissions.toTypedArray()
    }

    // 현재 권한 상태 확인
    val isAllPermissionsGranted = permissions.all { permissions ->
        ContextCompat.checkSelfPermission(context, permissions) == PackageManager.PERMISSION_GRANTED
    }

    // Launch 권한 요청
    LaunchedEffect(Unit) {
        if (!isAllPermissionsGranted) {
            // 권한 요청
            permissionLauncher.launch(permissions)
        } else {
            // next step
            handleGrantedPermission(context, onPermission)
        }
    }
}

/**
 * 권한 요청 실패 시 팝업 표시를 위한 컴포저블 함수
 *
 * - 권한 요청 실패 시 팝업 표시하는 용도로 사용
 */
private fun showPermissionDialog(context: Context, isVideo: Boolean) {
    val text = if (isVideo) {
        "미디어 관련(사진 및 동영상, 카메라, 마이크) 권한이 필요합니다."
    } else {
        "미디어 관련(사진 및 동영상, 카메라) 권한이 필요합니다."
    }

    AlertDialog.Builder(context)
        .setTitle("알림")
        .setMessage(text)
        .setPositiveButton("닫기") { _, _ -> }
        .setNegativeButton("설정 이동") { _, _ ->
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
        .show()
}

// ✅ 권한 허용 후 Android 14 선택적 접근 안내 처리
private fun handleGrantedPermission(
    context: Context,
    onPermissionGranted: () -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
        Toast.makeText(
            context,
            "권한은 허용됐지만 일부 사진만 선택된 상태일 수 있습니다.",
            Toast.LENGTH_LONG
        ).show()
    }
    onPermissionGranted()
}