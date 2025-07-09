package co.aos.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import co.aos.myutils.log.LogUtil

/**
 * 블루투스 관련 권한 처리
 * */
@Composable
fun BlueToothPermissionHandler(
    onPermission: () -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current

    // 요청할 권한 목록(Android 12이하 버전과 분리)
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        // Android 12미만 경우 위치 권한이 있어야 블루투스 스캔이 되기 때문에 해당 권한 요청이 필요하다!
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        val allGranted = permissionsResult.all { it.value }
        val deniedPermissions = permissionsResult.filterNot { it.value }
        LogUtil.e("TestLog", "deniedPermissions : $deniedPermissions")

        if (allGranted) {
            // 권한 허용
            onPermission()
        } else {
            // 권한 미 허용 시 디바이스 설정 메뉴로 이동
            showPermissionDialog(context)
        }
    }

    // 현재 권한 상태 확인
    val isAllPermissionsGranted = permissions.all { permissions ->
        ContextCompat.checkSelfPermission(context, permissions) == PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(Unit) {
        if (!isAllPermissionsGranted) {
            // 권한 요청
            permissionLauncher.launch(permissions)
        } else {
            onPermission()
        }
    }
}

/**
 * 권한 요청 실패 시 팝업 표시를 위한 컴포저블 함수
 *
 * - 권한 요청 실패 시 팝업 표시하는 용도로 사용
 *
 * @param context : 컨텍스트
 */
private fun showPermissionDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("알림")
        .setMessage("블루투스 권한이 필요합니다.")
        .setPositiveButton("닫기") { _, _ -> }
        .setNegativeButton("설정 이동") { _, _ ->
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
        .show()
}