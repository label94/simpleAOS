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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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

    // 권한 요청
    CameraPermissionHandler {
        viewModel.setEvent(NewOcrContract.Event.UpdateIsCameraGranted(it))
    }

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
                        CameraPreviewAndCapture(
                            context = context,
                            lifecycleOwner = lifecycleOwner,
                            onImageCaptured = { imageProxy ->
                                try {
                                    val bitmap = ImageUtils.imageProxyToBitmap(imageProxy)
                                    imageProxy.close()
                                    viewModel.setEvent(NewOcrContract.Event.OnCaptureBitmap(bitmap))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    LogUtil.e(LogUtil.OCR_LOG_TAG, "error : ${e.message}")
                                    imageProxy.close()
                                }
                            },
                            onError = {
                                LogUtil.e(LogUtil.OCR_LOG_TAG, "error : ${it.message}")
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
 * */
@Composable
fun CameraPreviewAndCapture(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onImageCaptured: (ImageProxy) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    // preview
    val preview = remember { PreviewView(context) }

    // imageCapture 설정
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val previewUseCase = androidx.camera.core.Preview.Builder().build().also {
            it.surfaceProvider = preview.surfaceProvider
        }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase, imageCapture)
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(LogUtil.OCR_LOG_TAG, "error : ${e.message}")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

        // 버튼 영역
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
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
                modifier = Modifier.padding(bottom = 24.dp).size(72.dp),
                shape = CircleShape
            ) {

            }
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