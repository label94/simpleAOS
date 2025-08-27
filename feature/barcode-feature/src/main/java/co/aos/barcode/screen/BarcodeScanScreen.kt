package co.aos.barcode.screen

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import co.aos.barcode.state.BarcodeScanContract
import co.aos.barcode.viewmodel.BarcodeViewModel
import co.aos.myutils.log.LogUtil
import co.aos.permission.CameraPermissionHandler
import co.aos.ui.theme.White
import com.google.mlkit.vision.common.InputImage

/**
 * barcode 스캔 화면
 * */
@OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScanScreen(
    viewModel: BarcodeViewModel = hiltViewModel()
) {
    // ui 상태
    val uiState by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    // 카메라 권한 관련
    val isGrantedCameraPermission = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    // 카메라 권한 요청
    CameraPermissionHandler {
        isGrantedCameraPermission.value = it
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (isGrantedCameraPermission.value) {
                // 카메라 권한이 허용 된 상태에서만 카메라 UI 표시
                CameraPreview(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    previewView = previewView,
                    isScanning = uiState.isScanning,
                    onImageCaptured = {
                        val image = it.image
                        if (image != null) {
                            LogUtil.i(LogUtil.BARCODE_SCAN_LOG_TAG, "image : $image")
                            val inputImage = InputImage.fromMediaImage(image, it.imageInfo.rotationDegrees)
                            viewModel.setEvent(BarcodeScanContract.Event.BarcodeDetected(inputImage))
                        }
                    },
                    onError = {
                        viewModel.setEvent(BarcodeScanContract.Event.Reset)
                    }
                )

                // 결과 처리
                uiState.result?.let { result ->
                    Column(
                        modifier = Modifier.fillMaxSize().background(Color(0x80000000)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("인식 결과 : $result", color = White)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.setEvent(BarcodeScanContract.Event.Reset)
                            }
                        ) {
                            Text("다시하기")
                        }
                    }
                }

                // 하단 스캔 버튼
                if (!uiState.isScanning && uiState.result == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 24.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Button(
                            onClick = {
                                viewModel.setEvent(BarcodeScanContract.Event.StartScanning)
                            }
                        ) {
                            Text("스캔 시작")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 카메라 프리뷰
 * - 하단 스캔 버튼 누르면, 스캔 시작
 * */
@Composable
private fun CameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    isScanning: Boolean,
    onImageCaptured: (ImageProxy) -> Unit,
    onError: (String) -> Unit,
) {
    // Analyzer를 기억해두기 위해 remember
    var analyzerUseCase: ImageAnalysis? by remember { mutableStateOf(null) }

    // 프레임 처리 중인지 확인하는 플래그
    var isProcessingFrame by remember { mutableStateOf(false) }

    val cameraProvider = remember { ProcessCameraProvider.getInstance(context).get() }
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val previewUseCase = remember {
        Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
    }

    // Preview는 최초 1회만 바인딩
    LaunchedEffect(Unit) {
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase)
        } catch (e: Exception) {
            onError("Preview binding error: ${e.message}")
        }
    }

    // isScanning 상태에 따라 Analyzer만 붙였다 뗐다
    LaunchedEffect(isScanning) {
        try {
            if (isScanning) {
                val analyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                analyzer.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                    // 중복으로 mlkit 연동 시, 중복 관련 오류가 발생하기 때문에 인식 중에는 중복 처리하지 않도록 분기 처리
                    if (!isProcessingFrame) {
                        isProcessingFrame = true
                        onImageCaptured(imageProxy) // mlkit 연동
                        isProcessingFrame = false
                    } else {
                        imageProxy.close()
                    }
                }

                // analyzer 바인드
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase, analyzer)
                analyzerUseCase = analyzer
            } else {
                // 현재 바인딩 된 analyzer 만 해제(해제할 analyzer를 전달하지 않으면, 모두 해제 되기 때문에 이 때 화면이 깜박이는 문제와 초기화 되는 문제 발생)
                // CameraX 내부에서 Analyzer는 한 번 바인딩되면 계속 살아 있기 때문에, setAnalyzer() 재호출로는 ML kit 콜백 호출이 안됨.
                // 그래서 스캔 시 Analyzer를 신규로 등록 하고, 스캔이 종료가 되면 Analyzer를 해제하는 작업이 필요!!!
                analyzerUseCase?.let { cameraProvider.unbind(it) }
                analyzerUseCase = null
                isProcessingFrame = false
            }
        } catch (e: Exception) {
            onError("Analyzer binding error: ${e.message}")
        }
    }

    // 카메라 프리뷰 추가
    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )
}