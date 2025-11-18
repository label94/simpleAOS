package co.aos.home.mypage.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.aos.common.noRippleClickable
import co.aos.common.showSnackBarMessage
import co.aos.home.mypage.state.MyPageContract
import co.aos.home.mypage.viewmodel.MyPageViewModel
import co.aos.home.topbar.MyPageTopBar
import co.aos.ui.theme.Black
import co.aos.ui.theme.GhostWhite
import co.aos.ui.theme.Red
import co.aos.ui.theme.White
import co.aos.utils.LocalProfileImgVector

/**
 * 마이페이지 UI
 * */
@Composable
fun MyPageScreen(
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: MyPageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    BackHandler { onBack.invoke() }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is MyPageContract.Effect.NavigateToLogin -> onNavigateToLogin.invoke()
                is MyPageContract.Effect.Toast -> {
                    showSnackBarMessage(snackBarHostState, coroutineScope, effect.msg)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MyPageTopBar(onBack = { onBack.invoke() }) },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        containerColor = GhostWhite,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            uiState.profile?.let {
                ProfileCard(
                    nickName = it.nickName,
                    id = it.id,
                    localProfileImgCode = it.localProfileImgCode
                )
            }

            Surface(
                shape = MaterialTheme.shapes.large,
                tonalElevation = 1.dp,
                color = White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    MenuListItem(
                        title = "비밀번호 변경",
                        icon = Icons.Outlined.LockReset,
                        onClick = { if (!uiState.loading) viewModel.setEvent(MyPageContract.Event.OnClickChangePassword) }
                    )
                    HorizontalDivider(color = GhostWhite)
                    MenuListItem(
                        title = "로그아웃",
                        icon = Icons.Outlined.Logout,
                        onClick = { if (!uiState.loading) viewModel.setEvent(MyPageContract.Event.OnClickLogout) }
                    )
                    HorizontalDivider(color = GhostWhite)
                    uiState.appInfo?.let {
                        MenuListItem(
                            title = "앱 버전",
                            icon = Icons.Outlined.Info,
                            onClick = { /* No action */ },
                            trailingContent = {
                                Text(text = "${it.versionName} (${it.versionCode})")
                            }
                        )
                        HorizontalDivider(color = GhostWhite)
                    }
                    MenuListItem(
                        title = "회원탈퇴",
                        icon = Icons.Outlined.DeleteForever,
                        textColor = Red,
                        onClick = { if (!uiState.loading) viewModel.setEvent(MyPageContract.Event.OnClickDeleteAccount) }
                    )
                }
            }

            uiState.error?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = Red)
            }

            if (uiState.loading) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            }
        }

        if (uiState.showChangePassword) {
            ChangePasswordDialog(
                currentId = uiState.profile?.id.orEmpty(),
                loading = uiState.loading,
                onDismiss = { viewModel.setEvent(MyPageContract.Event.OnDismissChangePassword) },
                onSubmit = { id, current, new, confirm ->
                    viewModel.setEvent(MyPageContract.Event.OnSubmitChangePassword(id, current, new, confirm))
                },
            )
        }

        if (uiState.showDeleteAccount) {
            DeleteAccountDialog(
                loading = uiState.loading,
                onDismiss = { viewModel.setEvent(MyPageContract.Event.OnDismissDeleteAccount) },
                onSubmit = { password, confirmed ->
                    viewModel.setEvent(MyPageContract.Event.OnSubmitDeleteAccount(password, confirmed))
                }
            )
        }
    }
}

/** 프로필 UI 영역 */
@Composable
private fun ProfileCard(
    nickName: String,
    id: String,
    localProfileImgCode: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val resId = LocalProfileImgVector.getLocalProfileImageVector(localProfileImgCode)
        Image(
            painter = painterResource(id = resId),
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )

        Text(
            text = nickName.ifBlank { "닉네임 미설정" },
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Text(
            text = id.ifBlank { "아이디 미설정" },
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** 공용 메뉴 리스트 아이템 */
@Composable
private fun MenuListItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = LocalContentColor.current,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** 비밀번호 변경 다이얼로그 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordDialog(
    currentId: String,
    loading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String) -> Unit
) {
    var idText by rememberSaveable { mutableStateOf(currentId) }
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    BasicAlertDialog(onDismissRequest = { if (!loading) onDismiss() }) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp,
            color = White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("비밀번호 변경", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = idText, onValueChange = { idText = it },
                    label = { Text("현재 아이디 (이메일)") },
                    enabled = !loading, singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = currentPassword, onValueChange = { currentPassword = it },
                    label = { Text("현재 비밀번호") },
                    enabled = !loading, singleLine = true, visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newPassword, onValueChange = { newPassword = it },
                    label = { Text("새 비밀번호") },
                    enabled = !loading, singleLine = true, visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword, onValueChange = { confirmPassword = it },
                    label = { Text("새 비밀번호 확인") },
                    singleLine = true, enabled = !loading, visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { if (!loading) onDismiss() }, enabled = !loading) { Text("취소") }
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(
                        onClick = { onSubmit(idText, currentPassword, newPassword, confirmPassword) },
                        enabled = !loading
                    ) { Text("변경") }
                }
            }
        }
    }
}

/** 회원탈퇴 다이얼로그 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteAccountDialog(
    loading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (currentPassword: String, confirmed: Boolean) -> Unit
) {
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var agreed by rememberSaveable { mutableStateOf(false) }

    BasicAlertDialog(onDismissRequest = { if (!loading) onDismiss() }) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp,
            color = White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("회원탈퇴", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    text = "회원탈퇴를 진행하면 모든 일기와 무드 데이터가 영구적으로 삭제되며 복구할 수 없어요.",
                    style = MaterialTheme.typography.bodySmall
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = agreed, onCheckedChange = { if (!loading) agreed = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("위 내용을 모두 이해했고, 탈퇴에 동의합니다.", style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(
                    value = currentPassword, onValueChange = { currentPassword = it },
                    label = { Text("현재 비밀번호") },
                    singleLine = true, enabled = !loading, visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { if (!loading) onDismiss() }, enabled = !loading) { Text("취소") }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { onSubmit(currentPassword, agreed) },
                        enabled = !loading && agreed
                    ) { Text("탈퇴하기") }
                }
            }
        }
    }
}
