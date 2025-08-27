package co.aos.ocr.presention.screen

import android.content.Context
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import co.aos.myutils.log.LogUtil
import co.aos.ocr.presention.state.NewOcrContract
import co.aos.ocr.presention.utils.ImageUtils
import co.aos.ocr.presention.viewmodel.NewOcrViewModel
import co.aos.permission.CameraPermissionHandler

/**
 *
 * */
@Composable
fun NewOcrScreen(
    viewModel: NewOcrViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = LocalActivity.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // preview
    val preview = remember { PreviewView(context) }

    var guideHeightRatio by remember { mutableFloatStateOf(0.32f) } // 드래그로 조절할 비율

    // 권한 요청
    CameraPermissionHandler {
        viewModel.setEvent(NewOcrContract.Event.UpdateIsCameraGranted(it))
    }

    // 백키 처리
    BackHandler {
        // 데이터 초기화
        viewModel.setEvent(NewOcrContract.Event.OnCancelCapture)

        // 콜백 함수 호출
        onBack.invoke()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (uiState.isCameraGranted) {
                        // 카메라 프리뷰
                        CameraPreviewAndCapture(
                            preview = preview,
                            context = context,
                            lifecycleOwner = lifecycleOwner,
                            onImageCaptured = { imageProxy ->
                                // 캡쳐 버튼 눌렸을 때 처리
                                try {
                                    // 1. ImageProxy → Bitmap 변환 + 회전 보정
                                    val bitmap = ImageUtils.imageProxyToBitmap(imageProxy)

                                    // 2. PreviewView 실제 크기 가져오기
                                    val previewWidth = preview.width
                                    val previewHeight = preview.height

                                    // 3. UI 가이드 박스 비율로 크롭
                                    val croppedBitmap = ImageUtils.cropByUiGuideBox(
                                        bitmap,
                                        previewWidth,
                                        previewHeight,
                                        guideWidthRatio = 0.8f, // 가이드 박스 가로 비율
                                        guideHeightRatio = guideHeightRatio // 가이드 박스 세로 비율
                                    )

                                    // 4. OCR 추출 작업 요청
                                    viewModel.setEvent(NewOcrContract.Event.OnCaptureBitmap(croppedBitmap))

                                    // 5. 카메라 프레임 메모리 해제(메모리 리소스 관리를 위함)
                                    imageProxy.close()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    LogUtil.e(LogUtil.OCR_LOG_TAG, "error : ${e.message}")
                                    imageProxy.close()
                                }
                            },
                            onError = {
                                LogUtil.e(LogUtil.OCR_LOG_TAG, "error : ${it.message}")
                            },
                            guideHeightRatio = guideHeightRatio,
                            onHeightUpdate = {
                                // 변경 된 가이드 라인의 세로 영역 업데이트
                                guideHeightRatio = it
                            }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("카메라 권한 필요")
                        }
                    }

                    // 결과 오버레이 : 촬영 된 비트맵이 있는 경우 이미지와 바운딩 박스 표시
                    uiState.lastBitmap?.let { bmp ->
                        // 반투명 오버레이로 결과 보여주기
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x80000000))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                            ) {
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "captured",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 400.dp)
                                )

                                Spacer(Modifier.height(8.dp))

                                uiState.result?.let { res ->
                                    // 단순 텍스트 나열
                                    Text("인식 결과 : ", style = MaterialTheme.typography.titleMedium)
                                    Text(res.fullText, modifier = Modifier.padding(top = 4.dp))
                                }

                                Spacer(Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = { viewModel.setEvent(NewOcrContract.Event.OnCancelCapture) }
                                    ) {
                                        Text("닫기")
                                    }
                                }
                            }
                        }
                    }// uiState
                }
            }
        }

        // 로딩 UI
        LoadingUI(
            isLoading = uiState.isLoading,
            error = uiState.error
        )
    }
}

/**
 * 카메라 화면 관련
 * @param context 컨텍스트
 * @param lifecycleOwner 라이프 사이클
 * @param preview 카메라 프리뷰 객체
 * @param onImageCaptured 이미지 캡쳐 콜백
 * @param onError 에러 콜백
 * @param guideHeightRatio 가이드 박스 세로 비율
 * @param onHeightUpdate 가이드 박스 세로 비율 업데이트 콜백
 * */
@Composable
private fun CameraPreviewAndCapture(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    preview: PreviewView,
    guideHeightRatio: Float,
    onImageCaptured: (ImageProxy) -> Unit,
    onHeightUpdate: (Float) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {

    // imageCapture 설정
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    // 컴포저블이 처음 호출 될 때 한번만 실행
    LaunchedEffect(Unit) {
        // 카메라X 관련 객체를 생성
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        // 카메라 프리뷰를 화면에 보여주는 용도로 사용
        val previewUseCase = androidx.camera.core.Preview.Builder().build().also {
            it.surfaceProvider = preview.surfaceProvider // 카메라 프레임을 PreviewView에 제공
        }

        // 후면 카메라 선택
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            // 기존 바인딩 제거
            cameraProvider.unbindAll()

            // CameraX가 기기의 카메라를 화면 프리뷰와 촬영 기능과 연동하는 작업
            // lifecycleOwner : Activity 수명과 연동 -> 화면이 destroy 되면 카메라 자동 해제
            // cameraSelector : lifecycle 에 등록할 기기의 카메라
            // previewUseCase, imageCapture : 실제 카메라 동작을 위하여 등록
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase, imageCapture)
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(LogUtil.OCR_LOG_TAG, "error : ${e.message}")
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            // preview 영역
            AndroidView(
                factory = {
                    preview
                },
                modifier = Modifier.fillMaxSize()
            ) { view ->
                // nothing extra
                (view.parent as? ViewGroup)?.clipToPadding = false
            }

            // OCR 추출 시 인식률을 높기이 위하여 가이드 라인이 지정 된 UI
            CropGuideOverlay(
                initialHeightRatio = guideHeightRatio,
                minRatio = 0.2f,
                maxRatio = 0.8f
            ) { newRatio ->
                onHeightUpdate.invoke(newRatio)
            }
        }

        // 버튼 영역
        Box(
            modifier = Modifier
                .fillMaxWidth().padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                onClick = {
                    // onClick - take picture as ImageProxy
                    imageCapture.takePicture(ContextCompat.getMainExecutor(preview.context), object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(imageProxy: ImageProxy) {
                            onImageCaptured(imageProxy)
                        }
                        override fun onError(exception: ImageCaptureException) {
                            super.onError(exception)
                            onError(exception)
                        }
                    })
                },
                modifier = Modifier.size(72.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoCamera,
                    contentDescription = "캡쳐 버튼",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * 크롭 할 수 있는 가이드 영역
 * */
@Composable
fun CropGuideOverlay(
    modifier: Modifier = Modifier,
    initialHeightRatio: Float = 0.3f,
    minRatio: Float = 0.1f,
    maxRatio: Float = 0.9f,
    onHeightRatioChange: (Float) -> Unit
) {
    var guideHeightRatio by remember { mutableFloatStateOf(initialHeightRatio) }

    Box(
        modifier = modifier.fillMaxSize()
            .pointerInput(Unit) {
                // 해당 영역 안에서 터치 및 드래그 감지
                detectVerticalDragGestures { _, dragAmount ->
                    // 드래그 된 거리(dragAmount)에 따라 가이드 라인의 세로 비율 변경
                    val change = dragAmount / size.height
                    guideHeightRatio = (guideHeightRatio + change).coerceIn(minRatio, maxRatio) // 높이 비율의 최대/최소를 지정
                    onHeightRatioChange(guideHeightRatio)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // 가이드 라인 표시
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val boxWidth = size.width * 0.8f
            val boxHeight = size.height * guideHeightRatio
            val left = (size.width - boxWidth) / 2
            val top = (size.height - boxHeight) / 2

            // 위쪽 반투명
            drawRect(
                color = Color(0x55000000),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, top)
            )

            // 아래쪽 반투명
            drawRect(
                color = Color(0x55000000),
                topLeft = Offset(0f, top + boxHeight),
                size = Size(size.width, size.height - (top + boxHeight))
            )

            // 빨간 테두리 박스
            drawRect(
                color = Color.Red,
                topLeft = Offset(left, top),
                size = Size(boxWidth, boxHeight),
                style = Stroke(width = 4f)
            )
        }
    }
}

/**
 * 로딩 UI
 * */
@Composable
fun LoadingUI(
    isLoading: Boolean,
    error: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}