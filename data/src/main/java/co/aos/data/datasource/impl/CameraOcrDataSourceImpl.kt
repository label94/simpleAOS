package co.aos.data.datasource.impl

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import co.aos.data.entity.TextResultEntity
import co.aos.data.datasource.CameraOcrDataSource
import co.aos.myutils.log.LogUtil
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 구글 mlkit 라이브러리로 부터 인식한 데이터를 반환하는 DataSource
 * */
class CameraOcrDataSourceImpl @Inject constructor() : CameraOcrDataSource {

    // ocr 인식 처리를 위한 객체 생성
    private val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    // 카메라 프로바이더 객체 생성
    suspend fun getCameraProvider(context: Context): ProcessCameraProvider =
        suspendCoroutine { cont ->
            val future = ProcessCameraProvider.getInstance(context)
            future.addListener(
                { cont.resume(future.get()) },
                ContextCompat.getMainExecutor(context)
            )
        }

    /** 인식 시작 */
    override fun startCameraOcr(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ): Flow<TextResultEntity> = channelFlow {
        val cameraProvider = getCameraProvider(context)

        // 프리뷰 연결
        val preview = Preview.Builder().build()
        preview.surfaceProvider = previewView.surfaceProvider

        // 이미지 인식을 위한 객체 builder 생성
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        // 이미지 인식 시작
        imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->

            @OptIn(ExperimentalGetImage::class)
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        LogUtil.d(LogUtil.OCR_LOG_TAG, "ocr start Success text => ${visionText.text}")

                        // 도메인으로 보내기 위해 데이터 가공
                        val result = TextResultEntity(
                            text = visionText.text,
                            blocks = visionText.textBlocks.map { it.text }
                        )
                        trySend(result)
                    }
                    .addOnFailureListener {
                        LogUtil.d(LogUtil.OCR_LOG_TAG, "ocr start Error => ${it.message}")
                        //trySend(TextResult("Error", emptyList()))
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)

        // flow 종료 시점에 바인드 해제
        awaitClose { cameraProvider.unbindAll() }
    }
}