package co.aos.domain.usecase.impl

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import co.aos.domain.model.TextResult
import co.aos.domain.repository.OcrRepository
import co.aos.domain.usecase.ObserveCameraOcrUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * OCR 관련 유스케이스 구현체
 * */
class ObserveCameraOcrUseCaseImpl @Inject constructor(
    private val repository: OcrRepository
): ObserveCameraOcrUseCase {

    /** 인식 시작 */
    override fun startObserving(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ): Flow<TextResult> {
        return repository.startObserving(context, lifecycleOwner, previewView)
    }
}