package co.aos.domain.repository

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import co.aos.domain.model.TextResult
import kotlinx.coroutines.flow.Flow

/**
 * OCR 관련 Repository
 * */
interface OcrRepository {
    /** 인식 시작 */
    fun startObserving(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ): Flow<TextResult>
}