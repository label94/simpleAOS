package co.aos.ocr.presention.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.ImageProxy

/**
 * Compose 에서 비트맵을 보여주기 위해 사용
 * */
object ImageUtils {

    /** ImageProxy → Bitmap (회전 보정 포함) */
    fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        // JPEG 디코딩
        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        // 회전 보정
        val rotationDegrees = image.imageInfo.rotationDegrees
        if (rotationDegrees != 0) {
            bitmap = rotateBitmap(bitmap, rotationDegrees)
        }

        return bitmap
    }

    /** 회전 보정 */
    private fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /** UI 가이드 박스 비율 기준으로 크롭 */
    fun cropByUiGuideBox(
        bitmap: Bitmap,
        previewViewWidth: Int,
        previewViewHeight: Int,
        guideWidthRatio: Float,
        guideHeightRatio: Float
    ): Bitmap {
        // 1. 가이드 박스 크기(px)
        val guideWidthPx = previewViewWidth * guideWidthRatio
        val guideHeightPx = previewViewHeight * guideHeightRatio

        // 2. 가이드 박스 시작 좌표(px)
        val guideStartX = (previewViewWidth - guideWidthPx) / 2
        val guideStartY = (previewViewHeight - guideHeightPx) / 2

        // 3. PreviewView → Bitmap 비율
        val scaleX = bitmap.width.toFloat() / previewViewWidth
        val scaleY = bitmap.height.toFloat() / previewViewHeight

        // 4. 실제 비트맵 좌표 변환
        val cropStartX = (guideStartX * scaleX).toInt()
        val cropStartY = (guideStartY * scaleY).toInt()
        val cropWidth = (guideWidthPx * scaleX).toInt()
        val cropHeight = (guideHeightPx * scaleY).toInt()

        // 5. 안전한 범위 보정
        val safeWidth = cropWidth.coerceAtMost(bitmap.width - cropStartX)
        val safeHeight = cropHeight.coerceAtMost(bitmap.height - cropStartY)

        return Bitmap.createBitmap(bitmap, cropStartX, cropStartY, safeWidth, safeHeight)
    }
}