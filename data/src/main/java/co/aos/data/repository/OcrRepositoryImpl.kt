package co.aos.data.repository

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import co.aos.data.datasource.CameraOcrDataSource
import co.aos.domain.model.TextResult
import co.aos.domain.repository.OcrRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * OCR 관련 Repository 구현체
 **/
class OcrRepositoryImpl @Inject constructor(
    private val dataSource: CameraOcrDataSource
): OcrRepository {

    /** 인식 시작 */
    override fun startObserving(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ): Flow<TextResult> {
        return dataSource.startCameraOcr(
            context, lifecycleOwner, previewView
        ).map {
            it.toDomain()
        }
    }
}