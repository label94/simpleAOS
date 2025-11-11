package co.aos.home.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.Refresh
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
import co.aos.ui.theme.Black
import co.aos.ui.theme.White

/**
 * 오늘의 영감 화면의 TopBar
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspirationTopBar(
    onBack: () -> Unit,
    onClear: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "하루의 마침표",
                style = MaterialTheme.typography.titleLarge,
                color = White,
                fontWeight = FontWeight.Bold
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
                onClick = { onClear.invoke() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "초기화",
                    tint = White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Black
        )
    )
}