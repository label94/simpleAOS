package co.aos.horizontalpicker

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import co.aos.common.getFontSize
import co.aos.common.noRippleClickable
import co.aos.horizontalpicker.data.PickerData
import co.aos.horizontalpicker.data.UiInfoData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 가로 형태의 피커
 *
 * @param uiInfoData : Picker UI 구성에 필요한 값
 * @param list : PickerData 리스트
 * @param initialItemIndex : 초기 선택된 인덱스
 * @param coroutineScope : 코루틴 스코프
 * @param onSelected : 선택된 아이템을 콜백
 * */
@Composable
fun HorizontalPicker(
    uiInfoData: UiInfoData,
    list: List<PickerData>,
    initialItemIndex: Int = 0,
    coroutineScope: CoroutineScope,
    onSelected: (Int) -> Unit
) {
    // 선택한 인덱스
    var selectedIndex by remember { mutableIntStateOf(initialItemIndex) }

    // 리스트 상태
    val listState = rememberLazyListState()

    // Picker ui 설정 값
    val itemWidthDp = uiInfoData.itemWidth
    val spacingDp = uiInfoData.spacingDp
    val totalItemWidthDp = itemWidthDp + spacingDp
    val density = LocalDensity.current
    val screenWidth = uiInfoData.screenWidth // 기기의 스크린 전체 넓이
    val sidePadding = (screenWidth - totalItemWidthDp) / 2 // (스크린 전체 - 아이템 가로 크기) / 2 => 리스트의 양 옆 패딩 값을 주기 위한 용도
    var snapJob: Job? by remember { mutableStateOf(null) } // 스냅 보정하기 위해 사용

    // 처음에 1번째 인덱스 위치에 스크롤 되도록 설정
    LaunchedEffect(Unit) {
        // 스크롤 이동
        listState.scrollToItem(selectedIndex)

        // selectedIndex 업데이트
        onSelected.invoke(selectedIndex)
    }

    // 사용자가 리스트를 스크롤 하고 있는 상태인지 확인
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress && list.isNotEmpty()) {
            // 스크롤 중인 상태가 아닌 경우에 중앙 정렬을 위한 스크롤 이동하는 로직 추가(스크롤 offset이 px 단위라서 px로 계산!)
            with(density) {
                val itemWidthPx = itemWidthDp.toPx()
                val spacingPx = spacingDp.toPx()
                val totalItemWidthPx = itemWidthPx + spacingPx

                val centerOffsetPx = listState.firstVisibleItemScrollOffset  + (itemWidthPx / 2) // 첫번째 아이템의 가운데 지점을 찾음
                val indexOffset = (centerOffsetPx / totalItemWidthPx).roundToInt() // 첫번째 아이템 기준으로 몇개의 아이템 만큼 이동했는지 계산
                val centerIndex = listState.firstVisibleItemIndex + indexOffset // 중앙 인덱스 계산

                if (centerIndex in list.indices) {
                    if (centerIndex != selectedIndex) {
                        selectedIndex = centerIndex
                        snapJob?.cancel()
                        snapJob = coroutineScope.launch {
                            listState.animateScrollToItem(centerIndex)

                            // selectedIndex 업데이트
                            onSelected.invoke(selectedIndex)
                        }
                    } else {
                        // 오차 보정(2px 이상의 오차 있을 경우 스냅 보정!)
                        val scrollOffsetPx = listState.firstVisibleItemScrollOffset.toFloat() // 첫 번째 보이는 아이템이 시작 위치에서 얼마나 스크롤 되어 있는지에 대한 값
                        val currentItemStartPx = centerIndex * totalItemWidthPx // 중앙 인덱스의 시작 위치
                        val actualScrollPx = listState.firstVisibleItemIndex * totalItemWidthPx + scrollOffsetPx // 현재 스크롤한 위치
                        val offsetDiff = abs(currentItemStartPx - actualScrollPx)
                        val threshold = with(density) { 2.dp.toPx() }

                        if (offsetDiff > threshold) {
                            snapJob?.cancel()
                            snapJob = coroutineScope.launch {
                                delay(50)
                                listState.scrollToItem(centerIndex)

                                // selectedIndex 업데이트
                                onSelected.invoke(selectedIndex)
                            }
                        }
                    }
                }
            }
        }
    }

    if (list.isNotEmpty()) {
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(start = sidePadding + 3.dp, end = sidePadding + 3.dp),
            horizontalArrangement = Arrangement.spacedBy(spacingDp),
            modifier = Modifier
                .fillMaxSize()
                .height(uiInfoData.lowHeight),
        ) {
            itemsIndexed(list) { index, item ->
                val isSelected = index == selectedIndex

                // 클릭한 아이템이 selectedItem과 일치하면 bold 처리 및 크기 애니메이션 설정
                val fontSize by animateFloatAsState(
                    targetValue = if (isSelected) uiInfoData.itemTextSelectSize else uiInfoData.itemTextNormalSize, // 선택된 항목과 그렇지 않을 때의 텍스트 크기 설정
                    animationSpec = tween(durationMillis = 300) // 애니메이션 지속 시간 설정
                )

                Box(
                    modifier = Modifier
                        .width(itemWidthDp)
                        .height(uiInfoData.itemHeight)
                        .noRippleClickable {
                            selectedIndex = index
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)

                                // selectedIdex 업데이트
                                onSelected.invoke(selectedIndex)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.title,
                        fontSize = getFontSize(fontSize),
                        color = if (isSelected) uiInfoData.selectTextColor else uiInfoData.normalTextColor,
                        textAlign = TextAlign.Center,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}