package com.thehyundai.setting_feature.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

/**
 * 상단 설정 App Bar UI
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSettingAppBar(
    onClose: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "설정",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 18.sp
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onClose
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
    )
}