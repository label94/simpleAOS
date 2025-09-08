package co.aos.barcode.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.RectF
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

/**
 * MLKit 카메라 매니저
 * */
class MlKitCameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val onBarcodeDetected: (Barcode?) -> Unit,
    private val roiProvider: () -> RectF,
) {
    private val cameraController = LifecycleCameraController(context)
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()
    private val barcodeScanner = BarcodeScanning.getClient(options)

    /** 인식기 바인딩 */
    @SuppressLint("ClickableViewAccessibility")
    fun bind(previewView: PreviewView) {
        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(context),
            MlKitAnalyzer(
                listOf(barcodeScanner),
                ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(context)
            ) { result ->
                val barcodes = result.getValue(barcodeScanner)
                val roi = roiProvider()

                if (barcodes == null || barcodes.isEmpty() || barcodes.first() == null) {
                    previewView.overlay.clear()
                    previewView.setOnTouchListener { _, _ -> false }
                    return@MlKitAnalyzer
                }

                val box = barcodes.first().boundingBox
                box?.let {
                    val rect = RectF(it)
                    if (roi.containsFully(rect)) {
                        // 전체 포함일 때만 허용
                        onBarcodeDetected.invoke(barcodes.first())
                    }
                }
            }
        )

        // Preview 및 컨트롤러 lifecycle 등록
        cameraController.bindToLifecycle(lifecycleOwner)
        previewView.controller = cameraController
    }
}

/** RectF가 포함하는지 확인 */
private fun RectF.containsFully(other: RectF): Boolean {
    return left <= other.left &&
            top <= other.top &&
            right >= other.right &&
            bottom >= other.bottom
}