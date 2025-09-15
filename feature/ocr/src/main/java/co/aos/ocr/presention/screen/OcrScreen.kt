package co.aos.ocr.presention.screen

import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import co.aos.common.showSnackBarMessage
import co.aos.ocr.presention.state.OcrContract
import co.aos.ocr.presention.viewmodel.OcrViewModel

/**
 * OCR 관련 화면
 **/
@Composable
fun OcrScreen(
    viewModel: OcrViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // 프리뷰
    val previewView = remember { PreviewView(context) }

    // 스백바 관련 상태
    val snackBarHostState = remember { SnackbarHostState() }

    // 1회성 이벤트 처리
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is OcrContract.Effect.ShowSnackBar -> {
                    // 스낵바 표시
                    showSnackBarMessage(
                        snackBarHostState = snackBarHostState,
                        coroutineScope = coroutineScope,
                        message = effect.message
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = {
            BottomAppBar {
                // 시작
                IconButton(
                    onClick = {
                        viewModel.setEvent(OcrContract.Event.StartCamera(
                            lifecycleOwner = lifecycleOwner,
                            previewView = previewView
                        ))
                    }
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "OCR 인식 시작")
                }

                // 중지
                IconButton(
                    onClick = {
                        viewModel.setEvent(OcrContract.Event.StopCamera)
                    }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "OCR 인식 종료")
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            AndroidView(
                factory = {
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(16.dp)
            ) {
                Text("OCR 결과", color = Color.White, fontSize = 20.sp)
                Text(uiState.textResultModel.text, color = Color.White, fontSize = 16.sp)
            }
        }
    }
}