package co.aos.home.detail.update.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.aos.common.noRippleClickable
import co.aos.common.showSnackBarMessage
import co.aos.home.detail.update.state.DiaryUpdateContract
import co.aos.home.detail.update.viewmodel.DiaryUpdateViewModel
import co.aos.home.editor.screen.MoodSelector
import co.aos.home.editor.screen.PinnedSwitch
import co.aos.home.editor.screen.TagSelector
import co.aos.home.topbar.DiaryUpdateTopBar
import co.aos.ui.theme.Black
import co.aos.ui.theme.GhostWhite
import co.aos.ui.theme.Magenta
import co.aos.ui.theme.Transparent

/** diary 수정 화면 UI */
@Composable
fun DiaryUpdateScreen(
    entryId: String,
    onClose: () -> Unit,
    onSaved: () -> Unit,
    viewModel: DiaryUpdateViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val effectFlow = viewModel.effect

    // focus / Keyboard controller
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val dismissKeyboard: () -> Unit = {
        focusManager.clearFocus()
        keyboard?.hide()
    }

    val snackBarHostState = remember { SnackbarHostState() }

    // 최초 로드
    LaunchedEffect(Unit) {
        viewModel.setEvent(DiaryUpdateContract.Event.Init(entryId))
    }

    // 1회성 이벤트 처리
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is DiaryUpdateContract.Effect.Close -> {
                    onClose.invoke()
                }
                is DiaryUpdateContract.Effect.Saved -> {
                    onSaved.invoke()
                }
                is DiaryUpdateContract.Effect.Toast -> {
                    showSnackBarMessage(
                        coroutineScope = coroutineScope,
                        snackBarHostState = snackBarHostState,
                        message = effect.msg,
                    )
                }
            }
        }
    }

    // 본문 자동 포커스(로드 완료 후 한 번 주고 싶으면 조건부)
    val bodyFocus = remember { FocusRequester() }
    LaunchedEffect(uiState.loading) {
        if (!uiState.loading && uiState.error == null) {
            bodyFocus.requestFocus()
        }
    }

    // UI 영역
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            DiaryUpdateTopBar(
                onBack = {
                    dismissKeyboard()
                    viewModel.setEvent(DiaryUpdateContract.Event.OnBackClick)
                },
                onSave = {
                    dismissKeyboard()
                    viewModel.setEvent(DiaryUpdateContract.Event.OnSaveClick)
                },
                isSave = !uiState.isSaving && !uiState.loading,
            )
        },
        containerColor = GhostWhite,
    ) { innerPadding ->
        when {
            // 로딩 UI
            uiState.loading -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // 에러 UI
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("오류: ${uiState.error}", color = Black, style = MaterialTheme.typography.titleMedium)
                }
            }

            else -> {
                // 본문 UI 표시
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .noRippleClickable {
                            dismissKeyboard() // 부코 컬럼 선택 시 ime 키보드 숨김 처리
                        }
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 날짜 (읽기 용)
                    Text(
                        text = "날짜 : ${uiState.dateText}",
                        color = Black,
                        style = MaterialTheme.typography.titleLarge
                    )

                    // 제목
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = { viewModel.setEvent(DiaryUpdateContract.Event.OnTitleChange(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        label = {
                            Text(
                                text = "제목",
                                color = Black,
                                style = MaterialTheme.typography.titleSmall
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
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Black)
                    )

                    // 본문
                    OutlinedTextField(
                        value = uiState.body,
                        onValueChange = { viewModel.setEvent(DiaryUpdateContract.Event.OnBodyChange(it)) },
                        modifier = Modifier
                            .fillMaxWidth().height(250.dp)
                            .verticalScroll(rememberScrollState()),
                        label = {
                            Text(
                                text = "오늘의 기록",
                                color = Black,
                                style = MaterialTheme.typography.titleSmall
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
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Black)
                    )

                    // 태그 UI 영역
                    TagSelector(
                        all = uiState.availableTags,
                        selected = uiState.selectedTags,
                        onToggle = {
                            dismissKeyboard()
                            viewModel.setEvent(DiaryUpdateContract.Event.OnToggleTag(it))
                        }
                    )

                    // 상단 고정(핀)
                    PinnedSwitch(
                        checked = uiState.pinned,
                        onCheckedChange = {
                            dismissKeyboard()
                            viewModel.setEvent(DiaryUpdateContract.Event.OnPinnedToggle(it))
                        }
                    )

                    if (uiState.isSaving) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}