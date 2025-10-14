package co.aos.user_feature.join.renewal.screen

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.aos.common.noRippleClickable
import co.aos.common.showSnackBarMessage
import co.aos.popup.CommonDialog
import co.aos.ui.theme.Black
import co.aos.ui.theme.LightGreen2
import co.aos.ui.theme.LightRed
import co.aos.ui.theme.Magenta
import co.aos.ui.theme.Transparent
import co.aos.ui.theme.White
import co.aos.user_feature.detail.dialog.ProfilePickerDialog
import co.aos.user_feature.join.renewal.state.SignUpContract
import co.aos.user_feature.join.renewal.viewmodel.SignUpViewModel
import co.aos.user_feature.utils.LocalProfileImgVector

/**
 * 회원가입 화면 UI
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserJoinScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onJoinSuccess: () -> Unit,
) {
    // ui 상태
    val uiState by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 팝업 관련
    var isShowDialog by remember { mutableStateOf(false) }

    // ime 포커스 관련
    val focusManager = LocalFocusManager.current

    // Back 버튼 처리
    BackHandler {
        focusManager.clearFocus() // 뒤로가기 시 ime 숨김
        onBack.invoke()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("회원가입", style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus() // 닫기 버튼 클릭 시 ime 숨김
                            onBack.invoke()
                        }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "닫기", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Black,
                    titleContentColor = White,
                )
            )
        },
        bottomBar = {
            JoinButtonLayout(
                modifier = Modifier.navigationBarsPadding(),
                isJoinEnable = uiState.isIdAvailable && uiState.nickNameAvailable,
                onJoin = {
                    // 회원가입 "완료" 버튼 클릭 시 ime 포커스 해제
                    focusManager.clearFocus()
                    viewModel.setEvent(SignUpContract.Event.ClickSignUp)
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        containerColor = White,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .noRippleClickable {
                    focusManager.clearFocus() // 화면 클릭 시 ime 숨김
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 15.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // 프로필 이미지 영역
                ProfileLayout(
                    selectProfileImageCode = uiState.localProfileImgCode,
                    onProfileClick = {
                        // 프로필 이미지 클릭 시 이벤트 처리
                        viewModel.setEvent(SignUpContract.Event.ShowProfilePicker)
                    }
                )

                // ID 입력 영역
                InputIdFieldLayout(
                    id = uiState.id,
                    isIdValid = uiState.isIdAvailable,
                    onValueChange = { viewModel.setEvent(SignUpContract.Event.UpdateID(it)) },
                    onDuplicateCheck = { viewModel.setEvent(SignUpContract.Event.ClickToIdCheck) }
                )

                // 닉네임 입력 영역
                InputNickNameFieldLayout(
                    nickname = uiState.nickName,
                    isNickNameValid = uiState.nickNameAvailable,
                    onValueChange = { viewModel.setEvent(SignUpContract.Event.UpdateNickName(it)) },
                    onDuplicateCheck = { viewModel.setEvent(SignUpContract.Event.ClickToNickName) }
                )

                // 패스워드 입력 영역
                InputPwdFieldLayout(
                    pwd = uiState.password,
                    isPwdVisible = uiState.isPasswordVisible,
                    onValueChange = { viewModel.setEvent(SignUpContract.Event.UpdatePassword(it)) },
                    onVisibleChange = { viewModel.setEvent(SignUpContract.Event.UpdatePasswordCheck(it)) }
                )
            }
        }
    }

    // 회원가입 완료 팝업
    if (isShowDialog) {
        JoinSuccessDialog(
            onClick = {
                isShowDialog = false
                onJoinSuccess.invoke()
            }
        )
    }

    // 프로필 피커 팝업
    ProfilePickerDialog(
        isVisibility = uiState.isShowProfilePicker,
        selectedCode = uiState.localProfileImgCode,
        onSelect = {
            viewModel.setEvent(SignUpContract.Event.UpdateLocalProfileImgCode(it))
        },
        onDismiss = {
            viewModel.setEvent(SignUpContract.Event.HideProfilePicker)
        }
    )

    // effect 처리
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is SignUpContract.Effect.ShowSnackBar -> {
                    showSnackBarMessage(
                        snackBarHostState = snackBarHostState,
                        message = effect.message,
                        coroutineScope = coroutineScope
                    )
                }
                is SignUpContract.Effect.SignUpSuccess -> {
                    viewModel.setEvent(SignUpContract.Event.InitData)
                    isShowDialog = true
                }
            }
        }
    }
}

/** 회원가입 버튼 영역 */
@Composable
private fun JoinButtonLayout(
    isJoinEnable: Boolean,
    modifier: Modifier = Modifier,
    onJoin: () -> Unit
) {
    Button(
        onClick = {
            onJoin.invoke()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RectangleShape,
        enabled = isJoinEnable,
        colors = ButtonDefaults.buttonColors(
            containerColor = Black,
            contentColor = White,
        )
    ) {
        Text("회원가입", style = MaterialTheme.typography.titleMedium)
    }
}

/** 프로필 이미지 영역 */
@Composable
private fun ProfileLayout(
    selectProfileImageCode: Int,
    onProfileClick: () -> Unit
) {
    val image = LocalProfileImgVector.getLocalProfileImageVector(selectProfileImageCode)

    Box(
        modifier = Modifier
            .size(140.dp)
            .background(Transparent)
            .border(2.dp, White, CircleShape)
            .noRippleClickable {
                // 클릭 시 이벤트 처리
                onProfileClick.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * ID 입력 레이아웃
 * */
@Composable
private fun InputIdFieldLayout(
    id: String,
    isIdValid: Boolean,
    onValueChange: (String) -> Unit,
    onDuplicateCheck: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = id,
            onValueChange = { onValueChange.invoke(it) },
            label = { Text("아이디", fontSize = MaterialTheme.typography.bodyMedium.fontSize) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = "ex) sample@example.com",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = Black.copy(alpha = 0.3f)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Black,
                unfocusedTextColor = Black,
                focusedContainerColor = Transparent,
                unfocusedContainerColor = Transparent,
                focusedIndicatorColor = Magenta,
                unfocusedIndicatorColor = Black,
                focusedLabelColor = Magenta,
                unfocusedLabelColor = Black
            )
        )

        IconButton(
            onClick = { onDuplicateCheck.invoke() },
            modifier = Modifier.size(40.dp).padding(top = 6.dp)
        ) {
            val imageVector = if (isIdValid) Icons.Default.Check else Icons.Default.Cancel
            val tintColor = if (isIdValid) LightGreen2 else LightRed
            Icon(
                imageVector = imageVector,
                contentDescription = "중복 확인",
                tint = tintColor
            )
        }
    }
}

/**
 * 패스워드 입력 영역
 * */
@Composable
private fun InputPwdFieldLayout(
    pwd: String,
    isPwdVisible: Boolean,
    onValueChange: (String) -> Unit,
    onVisibleChange: (Boolean) -> Unit
) {
    OutlinedTextField(
        value = pwd,
        onValueChange = { onValueChange.invoke(it) },
        label = { Text("비밀번호 입력", fontSize = MaterialTheme.typography.bodyMedium.fontSize) },
        singleLine = true,
        visualTransformation = if (isPwdVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (isPwdVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            IconButton(
                onClick = { onVisibleChange.invoke(!isPwdVisible) }
            ) {
                Icon(imageVector = icon, contentDescription = "비밀번호 표시")
            }
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = Black,
            unfocusedTextColor = Black,
            focusedContainerColor = Transparent,
            unfocusedContainerColor = Transparent,
            focusedIndicatorColor = Magenta,
            unfocusedIndicatorColor = Black,
            focusedLabelColor = Magenta,
            unfocusedLabelColor = Black
        ),
        placeholder = {
            Text(
                text = "비밀번호 6자리 이상 입력",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                color = Black.copy(alpha = 0.3f)
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}

/** 닉네임 입력 영역 */
@Composable
private fun InputNickNameFieldLayout(
    nickname: String,
    isNickNameValid: Boolean,
    onValueChange: (String) -> Unit,
    onDuplicateCheck: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = nickname,
            onValueChange = { onValueChange.invoke(it) },
            label = { Text("닉네임", fontSize = MaterialTheme.typography.bodyMedium.fontSize) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Black,
                unfocusedTextColor = Black,
                focusedContainerColor = Transparent,
                unfocusedContainerColor = Transparent,
                focusedIndicatorColor = Magenta,
                unfocusedIndicatorColor = Black,
                focusedLabelColor = Magenta,
                unfocusedLabelColor = Black
            )
        )

        IconButton(
            onClick = { onDuplicateCheck.invoke() },
            modifier = Modifier.size(40.dp).padding(top = 6.dp)
        ) {
            val imageVector = if (isNickNameValid) Icons.Default.Check else Icons.Default.Cancel
            val tintColor = if (isNickNameValid) LightGreen2 else LightRed
            Icon(
                imageVector = imageVector,
                contentDescription = "중복 확인",
                tint = tintColor
            )
        }
    }
}

/** 회원가입 완료 팝업 */
@Composable
private fun JoinSuccessDialog(
    onClick: () -> Unit,
) {
    CommonDialog(
        title = "안내",
        message = "회원가입이 완료 되었습니다.",
        confirmText = "확인",
        onConfirm = {
            // 회원가입 화면 닫기
            onClick.invoke()
        },
    )
}