package co.aos.barcode.screen

import android.content.Context
import android.graphics.Rect
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.aos.barcode.state.BarcodeScanRealtimeContract
import co.aos.barcode.viewmodel.BarcodeRealtimeScanViewModel
import co.aos.myutils.log.LogUtil
import co.aos.permission.CameraPermissionHandler
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Runnable

/**
 * Root Screen
 * */
@Composable
fun RealtimeRootScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "realtime_scan") {
        // 실시간 바코드 스캔 화면
        composable("realtime_scan") {
            RealtimeBarcodeScanScreen(
                onNavigateToResult = { barcode ->
                    navController.navigate("result/$barcode")
                }
            )
        }

        // 결과 화면
        composable(
            route = "result/{barcode}",
            arguments = listOf(navArgument("barcode") { type = NavType.StringType } )
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
            BarcodeScanResultScreen(
                barcode = barcode,
                onBack = {
                    navController.navigate("realtime_scan") {
                        popUpTo("result/{barcode}") { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * 리얼 타임 바코드 스캔 버전의 화면
 * */
@Composable
private fun RealtimeBarcodeScanScreen(
    viewModel: BarcodeRealtimeScanViewModel = hiltViewModel(),
    onNavigateToResult: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }

    // 카메라 권한 관련
    val isGrantedCameraPermission = remember { mutableStateOf(false) }

    // 카메라 권한 요청
    CameraPermissionHandler {
        isGrantedCameraPermission.value = it
    }

    // UI 영역
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isGrantedCameraPermission.value) {
                // 카메라 권한이 허용 된 상태에서만 UI 표시

                // 스캐너 상태 별 UI 표시 start
                when(uiState.currentScannerState) {
                    is BarcodeScanRealtimeContract.ScannerState.Idle -> {
                        viewModel.setEvent(BarcodeScanRealtimeContract.Event.StartScanning)
                    }
                    is BarcodeScanRealtimeContract.ScannerState.Scanning -> {
                        // 카메라 프리뷰 연동
                        RealtimeCameraPreview(
                            context = context,
                            lifecycleOwner = lifecycleOwner,
                            previewView = previewView,
                            onBarcodeScanned = { imageProxy ->
                                // 바코드 이미지 스캔 요청
                                viewModel.setEvent(BarcodeScanRealtimeContract.Event.ConverterInputImage(imageProxy))
                            }
                        )

                        // 중앙 가이드 라인
                        BarcodeScannerOverlay()
                    }
                    is BarcodeScanRealtimeContract.ScannerState.Success -> {
                        onNavigateToResult.invoke((uiState.currentScannerState as? BarcodeScanRealtimeContract.ScannerState.Success)?.barcode ?: "인식 실패")
                    }
                    is BarcodeScanRealtimeContract.ScannerState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val message = (uiState.currentScannerState as? BarcodeScanRealtimeContract.ScannerState.Error)?.message ?: "기타 오류"
                            Column {
                                Text("Error: $message", style = MaterialTheme.typography.bodyMedium)

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        // 스캔 재 시작
                                        viewModel.setEvent(BarcodeScanRealtimeContract.Event.StartScanning)
                                    }
                                ) {
                                    Text("다시 시도", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
                // 스캐너 상태 별 UI 표시 end
            }
        }
    }
}


/** Camera Preview */
@Composable
private fun RealtimeCameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onBarcodeScanned: (ImageProxy) -> Unit
) {
    DisposableEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)

        val listener = Runnable {
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                surfaceProvider = previewView.surfaceProvider
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            // 이미지 스캔 시작
            imageAnalyzer.setAnalyzer(executor) { imageProxy ->
                onBarcodeScanned.invoke(imageProxy)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.e(LogUtil.BARCODE_SCAN_LOG_TAG, "error : $e")
            }
        }

        cameraProviderFuture.addListener(listener, executor)

        onDispose {
            try {
                cameraProviderFuture.get().unbindAll()
            } catch (e: Exception) {}
        }
    }

    // 프리뷰 UI
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
    }
}

/** 가이드 라인 */
@Composable
private fun BarcodeScannerOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 중앙 가이드 박스
        Box(
            modifier = Modifier
                .size(250.dp) // 원하는 크기로 조정 가능
                .border(3.dp, Color.Red, RoundedCornerShape(4.dp))
        )
    }
}

@OptIn(ExperimentalGetImage::class)
private fun calculateGuideLine(
    context: Context, previewView: PreviewView, imageProxy: ImageProxy
) {
    val guideRect = run {
        val density = context.resources.displayMetrics.density
        val sizePx = (250.dp.value * density).toInt()
        Rect(
            (previewView.width - sizePx) / 2,
            (previewView.height - sizePx) / 2,
            (previewView.width + sizePx) / 2,
            (previewView.height + sizePx) / 2
        )
    }
}