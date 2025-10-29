package co.aos.home.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import co.aos.ui.theme.Black
import co.aos.ui.theme.White

/**
 * diary editor top bar
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEditorTopBar(
    onBack: () -> Unit,
    onSave: () -> Unit,
    isSave: Boolean
) {
    TopAppBar(
        title = {
            Text(
                text = "새 다이어리 작성",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = White
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { onBack.invoke() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "뒤로",
                    tint = White
                )
            }
        },
        actions = {
            IconButton(
                onClick = { onSave.invoke() },
                enabled = !isSave
            ) {
                Icon(
                    imageVector = Icons.Outlined.Save,
                    contentDescription = "저장",
                    tint = White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Black
        )
    )
}