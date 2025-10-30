package co.aos.home.topbar

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.aos.ui.theme.Black
import co.aos.ui.theme.White

/** Diary 상세 TopBar */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailTopBar(
    title: String,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = White
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { onBack.invoke() }
            ) {
                Icon(imageVector = Icons.Outlined.ArrowBackIosNew, contentDescription = "뒤로가기", tint = White)
            }
        },
        actions = {
            // 공유하기
            IconButton(
                onClick = { onShare.invoke() }
            ) {
                Icon(imageVector = Icons.Outlined.Share, contentDescription = "공유하기", tint = White, modifier = Modifier.size(25.dp))
            }

            // 수정
            IconButton(
                onClick = { onEdit.invoke() }
            ) {
                Icon(imageVector = Icons.Outlined.EditNote, contentDescription = "수정하기", tint = White, modifier = Modifier.size(25.dp))
            }

            // 삭제
            IconButton(
                onClick = { onDelete.invoke() }
            ) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "삭제하기", tint = White, modifier = Modifier.size(25.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Black,
        )
    )
}