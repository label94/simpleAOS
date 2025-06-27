package co.aos.horizontalpicker.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Picker UI 구성에 필요한 값
 *
 * @param itemWidth : 아이템의 너비
 * @param spacingDp : 아이템 간격
 * @param screenWidth : 화면 너비
 * @param lowHeight : LazyRow 의 높이
 * @param itemHeight : 아이템의 높이
 * @param itemTextNormalSize : 일반 텍스트 크기
 * @param itemTextSelectSize : 선택된 텍스트 크기
 * @param normalTextColor : 기본 텍스트 색상
 * @param selectTextColor : 선택된 텍스트 색상
 * */
data class UiInfoData(
    val itemWidth: Dp,
    val spacingDp: Dp,
    val screenWidth: Dp,
    val lowHeight: Dp,
    val itemHeight: Dp,
    val itemTextNormalSize: Float,
    val itemTextSelectSize: Float,
    val normalTextColor: Color,
    val selectTextColor: Color
)
