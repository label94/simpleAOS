package co.aos.barcode.screen

import android.annotation.SuppressLint
import android.graphics.RectF
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import co.aos.barcode.state.NewBarcodeScanContract
import co.aos.barcode.viewmodel.NewBarcodeScanViewModel
import co.aos.myutils.log.LogUtil
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.aos.barcode.utils.MlKitCameraManager
import co.aos.permission.CameraPermissionHandler
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 신규 바코드 스캔 관련 root 화면
 * */
@Composable
fun NewBarcodeScanRootScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "new_barcode_scan") {
        // 신규 바코드 스캔 UI 화면
        composable("new_barcode_scan") {
            NewBarcodeScanScreen(
                onNavigateToResult = { result ->
                    navController.navigate("new_scan_result/$result")
                }
            )
        }

        // 결과 UI 화면
        composable(
            route = "new_scan_result/{result}",
            arguments = listOf(navArgument("result") { type = NavType.StringType } )
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("result") ?: ""
            NewBarcodeScanResultScreen(
                result = barcode,
                onBack = {
                    navController.navigate("new_barcode_scan") {
                        popUpTo("new_scan_result/{result}") { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * 신규 바코드 스캔 화면(다이렉트 스캔)
 * */
@SuppressLint("ClickableViewAccessibility")
@Composable
fun NewBarcodeScanScreen(
    viewModel: NewBarcodeScanViewModel = hiltViewModel(),
    onNavigateToResult: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val previewView = remember { PreviewView(context) }
    val coroutineScope = rememberCoroutineScope()

    // ROI를 외부에서 상태로 보관 (Camera 쪽에 전달)
    var roi by remember { mutableStateOf(RectF()) }

    // 스캔 인식 처리를 위한 매니저
    val mlKitCameraManager = remember {
        MlKitCameraManager(
            context = context,
            lifecycleOwner = lifecycleOwner,
            onBarcodeDetected = { barcode ->
                if (!uiState.isDetected) {
                    // 바코드 스캔 시 상태 업데이트 이벤트 요청
                    viewModel.setEvent(NewBarcodeScanContract.Event.ScanOnStop)
                    viewModel.setEvent(NewBarcodeScanContract.Event.ScanOnDetected(barcode))

                    // 결과 화면으로 이동
                    coroutineScope.launch {
                        delay(500)
                        onNavigateToResult.invoke(uiState.barcodeStrResult ?: "인식 오류")
                    }
                }
            },
            roiProvider = { roi }
        )
    }

    // 카메라 권한 관련
    val isGrantedCameraPermission = remember { mutableStateOf(false) }
    CameraPermissionHandler {
        isGrantedCameraPermission.value = it
    }

    if (isGrantedCameraPermission.value) {
        // 권한 허용 됬을 때만 UI 구성
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { previewView },
            )

            LaunchedEffect(Unit) {
                // 인식기와 카메라 preview 바인딩
                mlKitCameraManager.bind(previewView)
            }

            // 현재 인식 된 영역
            if (uiState.barcodeRect != null) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val rect = uiState.barcodeRect
                    rect?.let { box ->
                        // PreviewView 좌표 기준 → Compose 캔버스 좌표로 변환 필요
                        drawRect(
                            color = Color.Red,
                            topLeft = Offset(box.left.toFloat(), box.top.toFloat()),
                            size = Size(box.width().toFloat(), box.height().toFloat()),
                            style = Stroke(width = 4f)
                        )
                    }
                }
            }

            // 오버레이
            ScannerOverlay(
                modifier = Modifier
                    .matchParentSize()   // 부모(Box)와 동일 크기
                    .zIndex(1f),         // AndroidView 위로
                boxSizeDp = 250.dp,
                onRoiChanged = { roi = it } // 계산된 ROI를 외부로 전달
            )
        }
    }
}

/** 스캐너 인식 영역(ROI) */
@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    boxSizeDp: Dp,
    onRoiChanged: (RectF) -> Unit
) {
    var parentSize by remember { mutableStateOf(IntSize.Zero) }
    var roi by remember { mutableStateOf(RectF()) }
    val density = LocalDensity.current
    val boxSizePx = with(density) { boxSizeDp.toPx() }

    Canvas(
        modifier = modifier
            // onSizeChanged 대신 onGloballyPositioned 사용 (초기 레이아웃에서도 안정적)
            .onGloballyPositioned { coords ->
                val s = coords.size
                if (s != parentSize) {
                    parentSize = s
                    val cx = s.width / 2f
                    val cy = s.height / 2f
                    val r = RectF(
                        cx - boxSizePx / 2f,
                        cy - boxSizePx / 2f,
                        cx + boxSizePx / 2f,
                        cy + boxSizePx / 2f
                    )
                    roi = r
                    onRoiChanged(r) // 카메라 쪽으로 ROI 전달
                }
            }
    ) {
        // ROI 테두리
        drawRect(
            color = Color.Green,
            topLeft = Offset(roi.left, roi.top),
            size = Size(roi.width(), roi.height()),
            style = Stroke(width = 4f)
        )
    }
}

@SuppressLint("ClickableViewAccessibility")
@Composable
fun TestBarcodeScannerScreen(
    viewModel: NewBarcodeScanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraController = remember { LifecycleCameraController(context) }
    val isDetected = remember { mutableStateOf(false) }

    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

    val previewView = remember { PreviewView(context) }
    val barcodeScanner = remember { BarcodeScanning.getClient(options) }

    // 바코드 결과 상태
    var detectedBarcodes by remember { mutableStateOf<List<Barcode>>(emptyList()) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { previewView },
    )

    LaunchedEffect(Unit) {
        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(context),
            MlKitAnalyzer(
                listOf(barcodeScanner),
                ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(context)
            ) { result ->
                val barcodeResults = result?.getValue(barcodeScanner)
                detectedBarcodes = barcodeResults ?: emptyList()

                if (barcodeResults == null || barcodeResults.isEmpty() || barcodeResults.first() == null) {
                    previewView.overlay.clear()
                    previewView.setOnTouchListener { _, _ -> false }
                    return@MlKitAnalyzer
                }

                if (!isDetected.value) {
                    val barcode = barcodeResults[0].rawValue
                    isDetected.value = true

                    barcode?.let {
                        LogUtil.e("TestLog", "barcode : $it")
                    }
                }
            }
        )
        cameraController.bindToLifecycle(lifecycleOwner)
        previewView.controller = cameraController
    }

    if (detectedBarcodes.isNotEmpty()) {
        // 오버레이: 바코드 위치 박스
        Canvas(modifier = Modifier.fillMaxSize()) {
            detectedBarcodes.forEach { barcode ->
                LogUtil.e("TestLog", "test barcode : ${barcode.rawValue}")

                barcode.boundingBox?.let { box ->
                    // PreviewView 좌표 기준 → Compose 캔버스 좌표로 변환 필요
                    drawRect(
                        color = Color.Red,
                        topLeft = Offset(box.left.toFloat(), box.top.toFloat()),
                        size = Size(box.width().toFloat(), box.height().toFloat()),
                        style = Stroke(width = 4f)
                    )
                }
            }
        }
    }
}
