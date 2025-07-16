package co.aos.data.datasource

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import co.aos.data.entity.TextResultEntity
import kotlinx.coroutines.flow.Flow

/**
 * OCR 관련 DataSource
 * */
interface CameraOcrDataSource {
    /** 인식 시작 */
    fun startCameraOcr(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ): Flow<TextResultEntity>
}