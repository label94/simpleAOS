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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.Output
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
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

    // 뒤로가기 키 처리
    BackHandler {
        onBack.invoke()
    }

    // 1회성 이벤트 관련 처리
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is MyPageContract.Effect.NavigateToLogin -> {
                    onNavigateToLogin.invoke()
                }
                is MyPageContract.Effect.Toast -> {
                    showSnackBarMessage(
                        coroutineScope = coroutineScope,
                        snackBarHostState = snackBarHostState,
                        message = effect.msg
                    )
                }
            }
        }
    }

    // UI
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            MyPageTopBar(
                onBack = { onBack.invoke() }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        containerColor = GhostWhite,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 프로필 카드 영역
            uiState.profile?.let { user ->
                ProfileCard(
                    nickName = user.nickName,
                    id = user.id,
                    localProfileImgCode = user.localProfileImgCode
                )
            }

            // 계정 관리 영역
            AccountSection(
                onClickChangePassword = {
                    viewModel.setEvent(MyPageContract.Event.OnClickChangePassword)
                },
                onClickLogout = {
                    viewModel.setEvent(MyPageContract.Event.OnClickLogout)
                },
                onClickDeleteAccount = {
                    viewModel.setEvent(MyPageContract.Event.OnClickDeleteAccount)
                },
                loading = uiState.loading
            )

            // 앱 정보
            uiState.appInfo?.let { appInfo ->
                AppInfoSection(
                    versionName = appInfo.versionName,
                    versionCode = appInfo.versionCode
                )
            }

            // 에러 메시지 (옵션)
            uiState.error?.let { msg ->
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = Red
                )
            }

            // 로딩 인디케이터(하단)
            if (uiState.loading) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            }
        }

        // 비밀번호 변경 다이얼로그
        if (uiState.showChangePassword) {
            ChangePasswordDialog(
                currentId = uiState.profile?.id.orEmpty(),
                loading = uiState.loading,
                onDismiss = {
                    viewModel.setEvent(MyPageContract.Event.OnDismissChangePassword)
                },
                onSubmit = { currentId, currentPassword, newPassword, confirmPassword ->
                    viewModel.setEvent(
                        MyPageContract.Event.OnSubmitChangePassword(
                            currentId,
                            currentPassword,
                            newPassword,
                            confirmPassword
                        )
                    )
                },
            )
        }

        // 회원탈퇴 다이얼로그
        if (uiState.showDeleteAccount) {
            DeleteAccountDialog(
                loading = uiState.loading,
                onDismiss = {
                    viewModel.setEvent(MyPageContract.Event.OnDismissDeleteAccount)
                },
                onSubmit = { currentPassword, confirmed ->
                    viewModel.setEvent(
                        MyPageContract.Event.OnSubmitDeleteAccount(
                            currentPassword,
                            confirmed
                        )
                    )
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
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val resId = LocalProfileImgVector.getLocalProfileImageVector(localProfileImgCode)
            Image(
                painter = painterResource(id = resId),
                contentDescription = "프로필 이미지",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = nickName.ifBlank { "닉네임 미설정" },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = id.ifBlank { "아이디 미설정" },
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** 계정 관련 UI 영역 */
@Composable
private fun AccountSection(
    onClickChangePassword: () -> Unit,
    onClickLogout: () -> Unit,
    onClickDeleteAccount: () -> Unit,
    loading: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("계정 관리", style = MaterialTheme.typography.titleSmall)

        // 비밀번호 변경
        ListItem(
            headlineContent = { Text("비밀번호 변경") },
            leadingContent = { Icon(Icons.Outlined.LockReset, contentDescription = null) },
            modifier = Modifier.noRippleClickable { if (!loading) onClickChangePassword() }
        )

        HorizontalDivider()

        // 로그아웃
        ListItem(
            headlineContent = { Text("로그아웃") },
            leadingContent = { Icon(Icons.Outlined.Output, contentDescription = null) },
            modifier = Modifier.noRippleClickable { if (!loading) onClickLogout() }
        )

        HorizontalDivider()

        // 회원탈퇴
        ListItem(
            headlineContent = {
                Text(
                    "회원탈퇴",
                    style = MaterialTheme.typography.titleSmall.copy(color = Red)
                )
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.DeleteForever,
                    contentDescription = null,
                    tint = Red
                )
            },
            modifier = Modifier.noRippleClickable {
                if (!loading) {
                    onClickDeleteAccount.invoke()
                }
            }
        )
    }
}

/** 앱 정보 UI 영역 */
@Composable
private fun AppInfoSection(
    versionName: String,
    versionCode: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("앱 정보", style = MaterialTheme.typography.titleSmall)

        ListItem(
            headlineContent = { Text("버전") },
            supportingContent = { Text("$versionName ($versionCode)") }
        )
    }
}

/** 회원탈퇴 다이얼로그 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordDialog(
    currentId: String,
    loading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (
        currentId: String,
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) -> Unit
) {
    var idText by rememberSaveable { mutableStateOf(currentId) }
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    // 기존 AlertDialog가 deprecated 되었기 때문에 BasicAlertDialog로 대체
    BasicAlertDialog(
        onDismissRequest = {
            if (!loading) {
                onDismiss.invoke()
            }
        },
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp,
            color = White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 타이틀
                Text(
                    text = "비밀번호 변경",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 이메일
                OutlinedTextField(
                    value = idText,
                    onValueChange = { idText = it },
                    label = { Text("현재 아이디 (이메일)") },
                    enabled = !loading,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // 현재 비밀번호
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("현재 비밀번호") },
                    singleLine = true,
                    enabled = !loading,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                // 새 비밀번호
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("새 비밀번호") },
                    singleLine = true,
                    enabled = !loading,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                // 새 비밀번호 확인
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("새 비밀번호 확인") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { if (!loading) onDismiss.invoke() },
                        enabled = !loading,
                    ) {
                        Text("취소")
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    TextButton(
                        onClick = {
                            onSubmit.invoke(
                                idText,
                                currentPassword,
                                newPassword,
                                confirmPassword
                            )
                        },
                        enabled = !loading,
                    ) {
                        Text("변경")
                    }
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

    BasicAlertDialog(
        onDismissRequest = {
            if (!loading) {
                onDismiss.invoke()
            }
        }
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp,
            color = White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 타이틀
                Text(
                    text = "회원탈퇴",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )

                // 안내 문구
                Text(
                    text = "회원탈퇴를 진행하면 모든 일기와 무드 데이터가 영구적으로 삭제되며 복구할 수 없어요.",
                    style = MaterialTheme.typography.bodySmall
                )

                // 동의 체크박스
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = agreed,
                        onCheckedChange = {
                            if (!loading) {
                                agreed = it
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "위 내용을 모두 이해했고, 탈퇴에 동의합니다.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // 현재 비밀번호 입력
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("현재 비밀번호") },
                    singleLine = true,
                    enabled = !loading,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { if (!loading) onDismiss.invoke() },
                        enabled = !loading
                    ) {
                        Text("취소")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = {
                            onSubmit.invoke(currentPassword, agreed)
                        },
                        enabled = !loading
                    ) {
                        Text("탈퇴하기")
                    }
                }
            }
        }
    }
}