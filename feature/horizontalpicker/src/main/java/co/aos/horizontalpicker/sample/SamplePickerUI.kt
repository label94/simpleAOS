package co.aos.horizontalpicker.sample

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import co.aos.horizontalpicker.HorizontalPicker
import co.aos.horizontalpicker.data.PickerData
import co.aos.horizontalpicker.data.UiInfoData
import co.aos.myutils.log.LogUtil

/**
 * 가로형 Picker
 *
 * - 샘플 용 예제 코드
 * */
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun HorizontalPickerScreen() {
    val list = mutableListOf<PickerData>()
    val coroutineScope = rememberCoroutineScope()

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp // 기기의 스크린 전체 넓이
    for (i in 1..10) {
        list.add(PickerData(i, "Item $i"))
    }

    // picker ui를 구성하는 설정 값
    val uiInfoData = UiInfoData(
        itemWidth = 100.dp,
        spacingDp = 6.dp,
        screenWidth = screenWidth,
        lowHeight = 100.dp,
        itemHeight = 80.dp,
        itemTextNormalSize = 14f,
        itemTextSelectSize = 16f,
        normalTextColor = Color.Black,
        selectTextColor = Color.Red
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 60.dp)
    ) {
        HorizontalPicker(
            uiInfoData = uiInfoData,
            list = list,
            initialItemIndex = 0,
            coroutineScope = coroutineScope,
            onSelected = {
                LogUtil.i("TestLog", "selected Item : $it")
            }
        )
    }
}