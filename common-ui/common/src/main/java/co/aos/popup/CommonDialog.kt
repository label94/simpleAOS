package co.aos.popup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * 공용 팝업
 * */
@Composable
fun CommonDialog(
    title: String? = null,
    message: String,
    confirmText: String = "확인",
    dismissText: String? = null,
    isDismissable: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            if (isDismissable) {
                onDismiss.invoke()
            }
        }
    ) {
        Surface(
            shape = RoundedCornerShape(15.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 타이틀 영역
                title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 바디 영역
                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                )

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // 왼쪽 버튼
                    dismissText?.let { dt ->
                        TextButton(
                            onClick = {
                                onDismiss.invoke()
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(text = dt, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                        }

                        VerticalDivider(
                            modifier = Modifier.fillMaxHeight(),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                        )
                    }

                    // 오른쪽 버튼
                    TextButton(
                        onClick = {
                            onConfirm.invoke()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = confirmText, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSimpleDialog() {
    var showDialog by remember { mutableStateOf(false) }

    CommonDialog(
        title = "확인",
        message = "정말 삭제하시겠습니까?",
        confirmText = "삭제",
        dismissText = "취소",
        onConfirm = {
            // 삭제 로직
            showDialog = false
        },
        onDismiss = {
            showDialog = false
        }
    )
}