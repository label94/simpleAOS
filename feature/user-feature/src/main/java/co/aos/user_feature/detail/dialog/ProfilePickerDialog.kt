package co.aos.user_feature.detail.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.aos.common.noRippleClickable
import co.aos.ui.theme.Black
import co.aos.ui.theme.DarkGreen
import co.aos.ui.theme.Magenta
import co.aos.ui.theme.Transparent
import co.aos.ui.theme.White
import co.aos.user_feature.utils.LocalProfileImgVector
import co.aos.user_feature.utils.UserConst

/**
 * 로컬 용 프로필 이미지 피커 팝업
 * - Full Screen
 * */
@Composable
fun ProfilePickerDialog(
    isVisibility: Boolean,
    selectedCode: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisibility) return

    Dialog(
        onDismissRequest = { onDismiss.invoke() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .background(White)
                    .padding(horizontal = 0.dp, vertical = 0.dp)
            ) {
                // 상단 바
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Black),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "프로필 선택",
                        style = MaterialTheme.typography.titleLarge,
                        color = White,
                        modifier = Modifier.padding(start = 10.dp)
                    )

                    // 닫기 버튼
                    IconButton(
                        onClick = {
                            onDismiss.invoke()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 컨텐츠 영역
                val items = listOf(
                    UserConst.UserProfileImage.PROFILE_FIRST,
                    UserConst.UserProfileImage.PROFILE_SECOND,
                    UserConst.UserProfileImage.PROFILE_THIRD,
                    UserConst.UserProfileImage.PROFILE_FOURTH,
                    UserConst.UserProfileImage.PROFILE_FIFTH,
                    UserConst.UserProfileImage.PROFILE_SIXTH,
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (row in 0 until 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (col in 0 until 3) {
                                val index = row * 3 + col
                                val profile = items[index]
                                ProfileChoiceItem(
                                    code = profile.code,
                                    isSelected = selectedCode == profile.code,
                                    onClick = {
                                        onSelect.invoke(profile.code)
                                    }
                                )
                            }
                        }
                    }
                } // column

                // 하단 안내 영역
                Text(
                    text = "원하는 프로필을 탭하면 즉시 적용 됩니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}

/** 프로필 아이템 UI */
@Composable
private fun ProfileChoiceItem(
    code: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val resId = LocalProfileImgVector.getLocalProfileImageVector(code)

    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(CircleShape)
            .background(Transparent)
            .noRippleClickable {
                onClick.invoke()
            }
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        // 선택 시 UI 설정
        if (isSelected) {
            // 선택
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(width = 6.dp, color = Magenta, shape = CircleShape)
            )
        } else {
            // 미 선택
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(Black.copy(alpha = 0.4f))
            )
        }
    }
}