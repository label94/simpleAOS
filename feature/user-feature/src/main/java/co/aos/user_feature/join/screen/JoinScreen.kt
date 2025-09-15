package co.aos.user_feature.join.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.aos.common.noRippleClickable
import co.aos.common.showSnackBarMessage
import co.aos.myutils.log.LogUtil
import co.aos.popup.CommonDialog
import co.aos.user_feature.join.state.JoinContract
import co.aos.user_feature.join.viewmodel.JoinViewModel
import coil3.compose.rememberAsyncImagePainter

/**
 * 회원가입 화면
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinScreen(
    viewModel: JoinViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onJoinSuccess: () -> Unit
) {
    // ui 상태
    val uiState by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 팝업 관련
    var showDialog by remember { mutableStateOf(false) }

    // ime 포커스 관련
    val focusManager = LocalFocusManager.current

    // 파일 선택 관련 런처
    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        LogUtil.i(LogUtil.JOIN_LOG_TAG, "fileLauncher code : ${result.resultCode} \n data : ${result.data}")

        // 결과 처리
        viewModel.setEvent(JoinContract.Event.ChooserResult(result.resultCode, result.data))
    }

    // 카메라 권한 요청
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isPermission ->
        LogUtil.i(LogUtil.JOIN_LOG_TAG, "cameraPermissionLauncher : $isPermission")

        // 카메라 권한 허용 업데이트
        viewModel.setEvent(JoinContract.Event.UpdateCameraPermissionGranted(isPermission))

        // 다시 chooser 실행을 위한 이벤트 요청
        viewModel.setEvent(JoinContract.Event.ClickProfileImage)
    }

    // Back 버튼 처리
    BackHandler {
        focusManager.clearFocus() // 뒤로가기 시 ime 숨김
        onBack.invoke()
    }

    Scaffold(
        // TopBar
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
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            JoinButtonLayout(
                modifier = Modifier.navigationBarsPadding(),
                isJoinEnable = uiState.isIdValid && (uiState.id.isNotEmpty() && uiState.password.isNotEmpty()),
                onJoin = {
                    // 회원가입 "완료" 버튼 클릭 시 ime 포커스 해제
                    focusManager.clearFocus()
                    viewModel.setEvent(JoinContract.Event.OnJoin)
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .noRippleClickable {
                    focusManager.clearFocus()
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
                Spacer(modifier = Modifier.height(12.dp))

                // 프로필 이미지 영역
                ProfileLayout(
                    profileImagePath = uiState.profileImagePath,
                    onProfileClick = {
                        focusManager.clearFocus()
                        viewModel.setEvent(JoinContract.Event.ClickProfileImage)
                    }
                )

                // ID 입력 영역
                InputIdFieldLayout(
                    id = uiState.id,
                    isIdValid = uiState.isIdValid,
                    onValueChange = {
                        // 값 변경 시 다시 중복체크를 위해 false 로 변경
                        viewModel.setEvent(JoinContract.Event.UpdateIdDuplicate(false))

                        // 값 변경
                        viewModel.setEvent(JoinContract.Event.OnUpdateId(it))
                    },
                    onDuplicateCheck = {
                        focusManager.clearFocus()
                        viewModel.setEvent(JoinContract.Event.CheckIdDuplicate)
                    }
                )

                // PWD 입력 영역
                InputPwdFieldLayout(
                    pwd = uiState.password,
                    isPwdVisible = uiState.passwordVisible,
                    onValueChange = {
                        viewModel.setEvent(JoinContract.Event.OnUpdatePassword(it))
                    },
                    onVisibleChange = {
                        viewModel.setEvent(JoinContract.Event.OnUpdatePasswordVisible(it))
                    }
                )

                // 닉네임 입력 영역
                InputNickNameFieldLayout(
                    nickname = uiState.nickname,
                    onValueChange = {
                        viewModel.setEvent(JoinContract.Event.OnUpdateNickname(it))
                    }
                )
            }
        }
    }

    // effect 처리
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is JoinContract.Effect.ShowSnackBar -> {
                    showSnackBarMessage(
                        snackBarHostState = snackBarHostState,
                        message = effect.message,
                        coroutineScope = coroutineScope
                    )
                }
                is JoinContract.Effect.JoinSuccess -> {
                    // 회원가입 성공 시 기존 데이터 초기화
                    viewModel.setEvent(JoinContract.Event.InitData)
                    showDialog = true
                }
                is JoinContract.Effect.RequestCameraPermission -> {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
                is JoinContract.Effect.ChooserResult -> {
                    fileLauncher.launch(effect.chooserIntent)
                }
            }
        }
    }

    // 회원가입 안내 팝업
    if (showDialog) {
        CommonDialog(
            title = "안내",
            message = "회원가입이 완료 되었습니다.",
            confirmText = "확인",
            onConfirm = {
                showDialog = false

                // 회원가입 화면 닫기
                onJoinSuccess.invoke()
            },
        )
    }
}

/**
 * 프로필 이미지 영역
 * */
@Composable
fun ProfileLayout(
    profileImagePath: String? = null,
    onProfileClick: () -> Unit = {}
) {
    val imgUriPath = profileImagePath?.toUri()

    Box(
        modifier = Modifier
            .size(140.dp)
            .background(Color.Transparent)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            .noRippleClickable {
                // 클릭 시 이벤트 처리
                onProfileClick.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        if (imgUriPath != null && imgUriPath.toString().isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(imgUriPath),
                contentDescription = "프로필 이미지",
                modifier = Modifier.fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "프로필 이미지(기본)",
                modifier = Modifier.size(70.dp).padding(top = 5.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * ID 입력 레이아웃
 * */
@Composable
fun InputIdFieldLayout(
    id: String,
    isIdValid: Boolean = false,
    onValueChange: (String) -> Unit,
    onDuplicateCheck: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = id,
            onValueChange = { onValueChange.invoke(it) },
            label = { Text("아이디") },
            singleLine = true,
            modifier = Modifier.weight(1f),
//            colors = TextFieldDefaults.colors(
//                focusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
//                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
//                cursorColor = MaterialTheme.colorScheme.primary
//            )
        )

        IconButton(
            onClick = { onDuplicateCheck.invoke() },
            modifier = Modifier.size(40.dp).padding(top = 6.dp)
        ) {
            val imageVector = if (isIdValid) Icons.Default.Check else Icons.Default.Cancel
            val tintColor = if (isIdValid) Color(0xFF4CAF50) else Color(0xFFF44336)
            Icon(
                imageVector = imageVector,
                contentDescription = "중복 체크",
                tint = tintColor
            )
        }
    }
}

/**
 * 패스워드 입력 영역
 * */
@Composable
fun InputPwdFieldLayout(
    pwd: String,
    isPwdVisible: Boolean,
    onValueChange: (String) -> Unit,
    onVisibleChange: (Boolean) -> Unit
) {
    OutlinedTextField(
        value = pwd,
        onValueChange = { onValueChange.invoke(it) },
        label = { Text("비밀번호") },
        singleLine = true,
        visualTransformation = if (isPwdVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (isPwdVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            IconButton(
                onClick = {  onVisibleChange.invoke(!isPwdVisible) }
            ) {
                Icon(icon, contentDescription = "Toggle password")
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

/** 닉네임 입력 영역 */
@Composable
fun InputNickNameFieldLayout(
    nickname: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = nickname,
        onValueChange = { onValueChange.invoke(it) },
        label = { Text("닉네임") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

/** 회원가입 버튼 영역 */
@Composable
fun JoinButtonLayout(
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
        enabled = isJoinEnable
    ) {
        Text("회원가입", style = MaterialTheme.typography.titleMedium)
    }
}
