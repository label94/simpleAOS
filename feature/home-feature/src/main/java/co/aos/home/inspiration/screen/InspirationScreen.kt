package co.aos.home.inspiration.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.aos.common.noRippleClickable
import co.aos.common.showSnackBarMessage
import co.aos.home.inspiration.state.InspirationContract
import co.aos.home.inspiration.viewmodel.InspirationViewModel
import co.aos.home.topbar.InspirationTopBar
import co.aos.home.utils.MoodCatalog
import co.aos.ui.theme.Black
import co.aos.ui.theme.GhostWhite
import co.aos.ui.theme.Magenta
import co.aos.ui.theme.Transparent
import co.aos.ui.theme.White

/** 오늘의 영감 화면 UI */
@Composable
fun InspirationScreen(
    onNavigateToWrite: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: InspirationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // ime 포커스 관련
    val focusManager = LocalFocusManager.current

    BackHandler {
        focusManager.clearFocus() // 뒤로가기 시 ime 포커스 클리어
        onBack.invoke()
    }

    // 1회성 이벤트 처리
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is InspirationContract.Effect.NavigateToWrite -> {
                    onNavigateToWrite.invoke(effect.preset)
                }
                is InspirationContract.Effect.Toast -> {
                    showSnackBarMessage(
                        coroutineScope = coroutineScope,
                        snackBarHostState = snackBarHostState,
                        message = effect.msg
                    )
                }
            }
        }
    }

    // UI 영역
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            InspirationTopBar(
                onBack = { onBack.invoke() },
                onClear = {
                    focusManager.clearFocus() // 상단 뒤로가기 버튼 클릭 시 ime 포커스 클리어
                    viewModel.setEvent(InspirationContract.Event.InitEvent)
                }
            )
        },
        bottomBar = {
            if (uiState.isShowLaunchedPromptsBtn) {
                BottomAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = GhostWhite,
                ) {
                    Button(
                        onClick = {
                            focusManager.clearFocus() // 생성 버튼 클릭 시 ime 포커스 클리어
                            viewModel.setEvent(InspirationContract.Event.OnLaunchedPrompts)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RectangleShape,
                        enabled = true,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Black,
                            contentColor = White
                        )
                    ) {
                        Text(text = "생성", style = MaterialTheme.typography.bodyLarge, color = White)
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        containerColor = GhostWhite
    ) { innerPadding ->
        when {
            uiState.prompts.isNotEmpty() -> {
                // 프롬프트 리스트
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.prompts) { line ->
                        PromptCard(
                            text = line,
                            onClick = {
                                viewModel.setEvent(InspirationContract.Event.OnPromptResultClicked(line))
                            }
                        )
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .noRippleClickable {
                            focusManager.clearFocus() // 컬럼 선택 시 ime 포커스 클리어
                        },
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 상단 설명
                    Text(
                        text = "지금 기분과 관심사를 선택하면 오늘의 너에게 어울리는 다이어리 시작 문장을 추천해 줄게요.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Black,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    // 기분 선택 UI
                    MoodSelectorRow(
                        selected = uiState.selectedMood,
                        onSelect = {
                            focusManager.clearFocus() // 기분 선택 시 ime 포커스 클리어
                            viewModel.setEvent(InspirationContract.Event.OnMoodSelected(it))
                        }
                    )

                    // 관심 키워드 입력
                    OutlinedTextField(
                        value = uiState.hint,
                        onValueChange = { text ->
                            viewModel.setEvent(InspirationContract.Event.OnHintChanged(text))
                        },
                        label = {
                            Text("요즘 자주 떠오르는 주제 (선택)", style = MaterialTheme.typography.bodySmall, color = Black)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
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

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // 로딩 UI 표시
        if (uiState.loading) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(GhostWhite),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

/** 기분 선택 영역 UI */
@Composable
private fun MoodSelectorRow(
    selected: Int?,
    onSelect: (Int?) -> Unit
) {
    val moods = MoodCatalog.MOOD_DATA_STRING_LIST

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 상단 텍스트
        Text(
            text = "오늘 기분을 선택해 주세요.",
            style = MaterialTheme.typography.labelLarge,
            color = Black
        )

        // 토글 UI 영역
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            moods.forEach { (score, label) ->
                val isSelected = (selected == score)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 40.dp)
                        .noRippleClickable {
                            onSelect.invoke(score)
                        },
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = if (isSelected) 4.dp else 0.dp,
                    color = when {
                        isSelected -> Magenta
                        else -> GhostWhite
                    },
                    border = if (isSelected) {
                        BorderStroke(1.dp, Magenta)
                    } else {
                        BorderStroke(1.dp, Black.copy(0.25f))
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // 좌측 동그라미 인디케이터 (radio 느낌)
                        Box(
                            modifier = Modifier.size(18.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) {
                                        White
                                    } else {
                                        Black.copy(alpha = 0.6f)
                                    },
                                    shape = CircleShape
                                ),
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier.align(Alignment.Center)
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(White)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // 텍스트
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected)
                                White
                            else
                                Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/** 개별 프롬프트 카드 */
@Composable
private fun PromptCard(
    text: String,
    onClick: () -> Unit
) {
    // "프롬프트 — 예시" 형식 분리
    val parts = text.split("—", limit = 2)
    val prompt = parts.getOrNull(0)?.trim().orEmpty()
    val example = parts.getOrNull(1)?.trim().orEmpty()

    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        color = GhostWhite,
        border = BorderStroke(1.dp, Black.copy(0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable {
                onClick.invoke()
            }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = prompt.ifBlank { text },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (example.isNotBlank()) {
                Text(
                    text = example,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text(
                    text = "이 문장으로부터 자유롭게 이어서 적어보세요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}