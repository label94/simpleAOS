package co.aos.home.editor.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.aos.common.noRippleClickable
import co.aos.common.showSnackBarMessage
import co.aos.home.editor.state.DiaryEditorContract
import co.aos.home.editor.viewmodel.DiaryEditorViewModel
import co.aos.home.topbar.DiaryEditorTopBar
import co.aos.home.utils.MoodCatalog
import co.aos.ui.theme.Black
import co.aos.ui.theme.GhostWhite
import co.aos.ui.theme.Magenta
import co.aos.ui.theme.Transparent
import co.aos.ui.theme.White

/**
 * diary 작성 화면
 * @param onClose : 닫기 처리를 위한 콜백
 * @param onSaved : 저장 후 처리를 위한 콜백
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEditorScreen(
    viewModel: DiaryEditorViewModel = hiltViewModel(),
    onClose: () -> Unit,
    onSaved: () -> Unit,
) {
    // ui 상태 관련
    val uiState by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    // 컨텐츠 영역 포커스 관련
    val contentFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { contentFocus.requestFocus() }

    // ime 포커스 관련
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    // 공용: 바깥을 탭하면 포커스/키보드 해제
    val dismissKeyboard: () -> Unit = {
        focusManager.clearFocus()
        keyboard?.hide()
    }

    // 1회성 이벤트 처리
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is DiaryEditorContract.Effect.Close -> {
                    onClose.invoke()
                }
                is DiaryEditorContract.Effect.SavedAndClose -> {
                    onSaved.invoke()
                }
                is DiaryEditorContract.Effect.Toast -> {
                    // 스낵바 표시
                    showSnackBarMessage(
                        snackBarHostState = snackBarHostState,
                        coroutineScope = coroutineScope,
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
            DiaryEditorTopBar(
                onBack = {
                    dismissKeyboard() // 뒤로 가기 시에도 ime 키보드 숨김 처리
                    viewModel.setEvent(DiaryEditorContract.Event.OnBackClick)
                },
                onSave = {
                    dismissKeyboard() // 저장 선택 시에도 ime 키보드 숨김 처리
                    viewModel.setEvent(DiaryEditorContract.Event.OnSaveClick)
                },
                isSave = uiState.isSaving
            )
        },
        containerColor = GhostWhite,
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .noRippleClickable {
                    dismissKeyboard() // 부모 컬럼 선택 시에도 ime 키보드 숨김 처리
                }
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 제목
            OutlinedTextField(
                value = uiState.title,
                onValueChange = {
                  viewModel.setEvent(DiaryEditorContract.Event.OnTitleChange(it))
                },
                label = {
                    Text(
                        text = "제목",
                        color = Black,
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(contentFocus),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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

            // 본문 내용
            OutlinedTextField(
                value = uiState.body,
                onValueChange = {
                    viewModel.setEvent(DiaryEditorContract.Event.OnBodyChange(it))
                },
                label = {
                    Text(
                        text = "오늘의 기록",
                        color = Black,
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                modifier = Modifier
                    .fillMaxWidth().height(250.dp)
                    .verticalScroll(rememberScrollState()),
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
                    dismissKeyboard() // 태그 선택 시에도 ime 키보드 숨김 처리
                    viewModel.setEvent(DiaryEditorContract.Event.OnToggleTag(it))
                }
            )

            // 상단 고정(핀)
            PinnedSwitch(
                checked = uiState.pinned,
                onCheckedChange = {
                    dismissKeyboard() // 핀 선택 시에도 ime 키보드 숨김 처리
                    viewModel.setEvent(DiaryEditorContract.Event.OnPinnedToggle(it))
                }
            )

            if (uiState.isSaving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/** 무드 선택 관련 UI */
@Composable
fun MoodSelector(
    selected: Int?,
    onPick: (Int?) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "무드",
            style = MaterialTheme.typography.titleSmall,
            color = Black
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val moods = MoodCatalog.MOOD_DATA_LIST
            moods.forEach { (v, emoji) ->
                FilterChip(
                    selected = selected == v,
                    onClick = { onPick(if (selected == v) null else v) },
                    label = { Text(emoji) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Magenta,
                    )
                )
            }
        }
    }
}

/** 태그 선택(사전 정의, 다중 토글) */
@Composable
fun TagSelector(
    all: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("태그", style = MaterialTheme.typography.titleSmall, color = Black)
        if (all.isEmpty()) {
            Text(
                "사용 가능한 태그가 없습니다.",
                color = Black
            )
            return
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            all.forEach { tag ->
                FilterChip(
                    selected = tag in selected,
                    onClick = { onToggle.invoke(tag) },
                    label = { Text(tag, style = MaterialTheme.typography.bodySmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Magenta,
                        selectedLabelColor = White,
                        labelColor = Black,
                    )
                )
            }
        }
    }
}

/** 상단 고정 핀 UI */
@Composable
fun PinnedSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.PushPin,
            contentDescription = null,
        )
        Text("상단 고정", style = MaterialTheme.typography.titleSmall, color = Black)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange.invoke(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = Magenta,
                uncheckedThumbColor = Black,
                uncheckedTrackColor = White
            )
        )
    }
}