package co.aos.user_feature.login.legacy.screen

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.aos.common.showSnackBarMessage
import co.aos.user_feature.login.legacy.model.LoginInfoModel
import co.aos.user_feature.login.legacy.state.LoginContract
import co.aos.user_feature.login.legacy.viewmodel.LoginViewModel
import co.aos.user_feature.utils.UserConst

/**
 * 로그인 화면 UI
 *
 * @onLoginSuccess 로그인 성공 시 호출되는 콜백
 * @onShowSnackBar snack bar 표시 시 호출되는 콜백
 * @onMoveUserJoinPage 회원가입 페이지로 이동 시 호출되는 콜백
 * */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (LoginInfoModel) -> Unit,
    onMoveUserJoinPage: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    // snack bar 상태
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 키보드 관련 상태
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(PaddingValues())
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
                        viewModel.setEvent(LoginContract.Event.UpdateId(it))
                    },
                    label = { Text("아이디") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 비밀번호 입력 영역
                OutlinedTextField(
                    value = uiState.value.password,
                    onValueChange = {
                        // 비밀번호 상태 업데이트
                        viewModel.setEvent(LoginContract.Event.UpdatePassword(it))
                    },
                    label = { Text("비밀번호") },
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
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 자동 로그인 활성화 유무 영역
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.value.isAutoLoginEnable,
                        onCheckedChange = {
                            // 자동 로그인 체크 상태 변경
                            viewModel.setEvent(LoginContract.Event.UpdateIsAutoLogin(it))
                        }
                    )
                    Text(text = "자동 로그인")
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 로그인 버튼 영역
                Button(
                    onClick = {
                        viewModel.setEvent(LoginContract.Event.RequestLogin)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = uiState.value.loginState !is LoginContract.LoginState.Loading
                ) {
                    if (uiState.value.loginState is LoginContract.LoginState.Loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
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
                        viewModel.setEvent(LoginContract.Event.MoveUserJoinPage)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "아직 계정이 없으신가요? 지금 회원가입 하세요!")
                }
            }
        }
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is LoginContract.Effect.ShowSnackBar -> {
                    // snack bar 표시
                    showSnackBarMessage(
                        snackBarHostState = snackBarHostState,
                        coroutineScope = coroutineScope,
                        message = effect.message
                    )
                }
                is LoginContract.Effect.MovePage -> {
                    // 페이지 이동
                    when(effect.page) {
                        UserConst.UserPage.JOIN.pageName -> {
                            onMoveUserJoinPage.invoke()
                        }
                    }
                }
                is LoginContract.Effect.LoginComplete -> {
                    // 로그인 완료
                    onLoginSuccess.invoke(effect.loginInfo)
                }
            }
        }
    }
}