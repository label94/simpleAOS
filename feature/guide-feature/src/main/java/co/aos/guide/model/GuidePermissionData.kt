package co.aos.guide.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 접근 권한 안내 화면을 표시할 데이터 set
 * */
data class GuidePermissionData(
    val icon: ImageVector,
    val name: String,
    val description: String,
    val isOptional: Boolean
)
