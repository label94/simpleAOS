package co.aos.popup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 공용 하단 바텀 시트
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss.invoke()
        },
        sheetState = sheetState,
        dragHandle = {
            // 기본 드래그 핸들
            BottomSheetDefaults.DragHandle()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.padding(16.dp))
            }

            // 호출 하는 쪽에서 내용 주입
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewScreen() {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // 중간 단계 없이 전체 확장만
    )
    var showBottomSheet by remember { mutableStateOf(false) }

    CommonBottomSheet(
        sheetState = sheetState,
        onDismiss = { showBottomSheet = false },
        title = "옵션 선택"
    ) {
        Text("첫 번째 옵션")
        Spacer(Modifier.height(8.dp))
        Text("두 번째 옵션")
        Spacer(Modifier.height(8.dp))
        Button(onClick = { showBottomSheet = false }) {
            Text("닫기")
        }
    }
}