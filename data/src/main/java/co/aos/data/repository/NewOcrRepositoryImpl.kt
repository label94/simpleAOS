package co.aos.data.repository

import android.graphics.Bitmap
import android.graphics.Rect
import co.aos.domain.model.NewOcrResult
import co.aos.domain.model.NewOcrTextBlockModel
import co.aos.domain.repository.NewOcrRepository
import co.aos.myutils.log.LogUtil
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 신규 OCR 관련 - OCR Repository 구현체
 * */
class NewOcrRepositoryImpl @Inject constructor(): NewOcrRepository {

    private val recognizer by lazy {
        // 한국어 최적화 recognizer
        TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun recognizeFromBitMap(bitmap: Bitmap): NewOcrResult {
        return suspendCancellableCoroutine { cont ->
            val image = InputImage.fromBitmap(bitmap, 0)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // 성공
                    val blocks = visionText.textBlocks.mapNotNull { block ->
                    val box = block.boundingBox ?: return@mapNotNull null

                    LogUtil.i(LogUtil.OCR_LOG_TAG, "text : ${block.text}")
                    NewOcrTextBlockModel(block.text, Rect(box), null)
                }
                cont.resume(NewOcrResult(visionText.text, blocks))
            }
                .addOnFailureListener { e->
                    // 실패
                    LogUtil.e(LogUtil.OCR_LOG_TAG, "error : ${e.message}")
                    cont.resumeWithException(e)
                }
        }
    }
}