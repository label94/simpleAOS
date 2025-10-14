package co.aos.user_feature.login.renewal.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.aos.common.noRippleClickable
import co.aos.common.showSnackBarMessage
import co.aos.domain.model.User
import co.aos.ui.theme.Black
import co.aos.ui.theme.Magenta
import co.aos.ui.theme.Transparent
import co.aos.ui.theme.White
import co.aos.user_feature.login.renewal.state.AuthContract
import co.aos.user_feature.login.renewal.viewmodel.AuthViewModel
import co.aos.user_feature.utils.UserConst

/**
 * UserLogin UI 화면
 * */
@Composable
fun UserLoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onMoveUserJoinPage: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    // snack bar 상태
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 키보드 관련 상태
    val keyboardController = LocalSoftwareKeyboardController.current

    // ime 포커스 관련
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(PaddingValues())
                .noRippleClickable {
                    focusManager.clearFocus()
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "로그인",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 아이디 입력 영역
                OutlinedTextField(
                    value = uiState.value.id,
                    onValueChange = {
                        // id 상태 업데이트
                        viewModel.setEvent(AuthContract.Event.UpdateID(it.trim()))
                    },
                    label = { Text("아이디", fontSize = MaterialTheme.typography.bodyMedium.fontSize) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
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
                            text = "아이디를 입력하세요.",
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            color = Black.copy(alpha = 0.3f)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 비밀번호 입력 영역
                OutlinedTextField(
                    value = uiState.value.password,
                    onValueChange = {
                        // 비밀번호 상태 업데이트
                        viewModel.setEvent(AuthContract.Event.UpdatePassword(it.trim()))
                    },
                    label = { Text("비밀번호", fontSize = MaterialTheme.typography.bodyMedium.fontSize) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
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
                            text = "비밀번호를 입력하세요.",
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            color = Black.copy(alpha = 0.3f)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(23.dp))

                // 로그인 버튼 영역
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.setEvent(AuthContract.Event.ClickSignIn)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Black,
                        contentColor = White
                    ),
                    shape = RectangleShape,
                    enabled = !uiState.value.isLoading
                ) {
                    if (uiState.value.isLoading) {
                        CircularProgressIndicator(
                            color = White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(text = "로그인")
                    }
                }

                // 회원가입 버튼 영역
                TextButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.setEvent(AuthContract.Event.MoveToSignUpScreen)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "아직 계정이 없으신가요? 지금 회원가입 하세요!", color = Black, fontSize = 13.sp)
                }
            }
        }
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is AuthContract.Effect.ShowSnackBar -> {
                    // snack bar 표시
                    showSnackBarMessage(
                        snackBarHostState = snackBarHostState,
                        coroutineScope = coroutineScope,
                        message = effect.message
                    )
                }
                is AuthContract.Effect.MovePage -> {
                    // 페이지 이동
                    when(effect.page) {
                        UserConst.UserPage.JOIN.pageName -> {
                            onMoveUserJoinPage.invoke()
                        }
                    }
                }
                is AuthContract.Effect.LoginSuccess -> {
                    // 로그인 완료
                    onLoginSuccess.invoke()
                }
            }
        }
    }
}