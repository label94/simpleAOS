package co.aos.home.detail.load.screen

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import co.aos.common.showSnackBarMessage
import co.aos.home.bottombar.Routes
import co.aos.home.detail.load.state.DiaryDetailContract
import co.aos.home.detail.load.viewmodel.DiaryDetailViewModel
import co.aos.home.detail.model.DiaryDetail
import co.aos.home.topbar.DiaryDetailTopBar
import co.aos.popup.CommonDialog
import co.aos.ui.theme.Black
import co.aos.ui.theme.DarkGray
import co.aos.ui.theme.GhostWhite
import co.aos.ui.theme.Red
import co.aos.ui.theme.White
import java.time.format.DateTimeFormatter

/** 다이어리 상세 화면 UI */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailScreen(
    entryId: String,
    onClose: () -> Unit,
    onRefreshRequest: () -> Unit,
    onEdit: (String) -> Unit,
    navController: NavHostController,
    viewModel: DiaryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 뒤로 가기 이벤트
    BackHandler {
        if (uiState.isRefresh) {
            onRefreshRequest.invoke()
        } else {
            onClose.invoke()
        }
    }

    // 최초 로드 이벤트 호출
    LaunchedEffect(entryId) {
        viewModel.setEvent(DiaryDetailContract.Event.UpdateRequestRefresh(false))
        viewModel.setEvent(DiaryDetailContract.Event.Load(entryId))
    }

    // 수정 화면에서 다시 상세로 돌아올 때 refresh 플래그 감지
    val currentBackStackEntry = navController.currentBackStackEntry
    val refreshFlag = currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow(Routes.REFRESH_DETAIL, false)
        ?.collectAsState()

    LaunchedEffect(refreshFlag) {
        if (refreshFlag?.value == true) {
            viewModel.setEvent(DiaryDetailContract.Event.Load(entryId))
            viewModel.setEvent(DiaryDetailContract.Event.UpdateRequestRefresh(true))
            currentBackStackEntry.savedStateHandle[Routes.REFRESH_DETAIL] = false
        }
    }

    // 1회성 이벤트 제어
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is DiaryDetailContract.Effect.Toast -> {
                    showSnackBarMessage(snackBarHostState, coroutineScope, effect.msg)
                }
                is DiaryDetailContract.Effect.Close -> {
                    if (uiState.isRefresh) {
                        onRefreshRequest.invoke()
                    } else {
                        onClose.invoke()
                    }
                }
                is DiaryDetailContract.Effect.Edit -> {
                    onEdit(effect.entryId)
                }
                is DiaryDetailContract.Effect.ShareText -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, effect.text)
                    }
                    context.startActivity(Intent.createChooser(intent, "공유하기"))
                }
            }
        }
    }

    // 삭제 확인 팝업
    if (uiState.showDeleteConfirm) {
        DetailDeleteDialog(
            onConfirm = { viewModel.setEvent(DiaryDetailContract.Event.OnDeleteConfirm) },
            onDismiss = { viewModel.setEvent(DiaryDetailContract.Event.OnDeleteCancel) }
        )
    }

    // UI 영역
    Scaffold(
        topBar = {
            DiaryDetailTopBar(
                title = "다이어리 상세",
                onBack = { viewModel.setEvent(DiaryDetailContract.Event.OnBackClick) },
                onEdit = { viewModel.setEvent(DiaryDetailContract.Event.OnEditClick) },
                onDelete = { viewModel.setEvent(DiaryDetailContract.Event.OnDeleteClick) },
                onShare = { viewModel.setEvent(DiaryDetailContract.Event.OnShareClick) }
            )
        },
        floatingActionButton = {
            uiState.entry?.let {
                val isPinned = it.pinned
                FloatingActionButton(
                    onClick = {},
                    containerColor = if (isPinned) Black else DarkGray,
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "상단 고정",
                        tint = White
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        containerColor = GhostWhite
    ) { innerPadding ->
        when {
            uiState.loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "페이지 오류 입니다.\n잠시후 다시 이용해주세요.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            uiState.entry != null -> {
                DetailBody(
                    data = uiState.entry!!,
                    modifier = Modifier.padding(innerPadding).fillMaxSize()
                )
            }
        }
    }
}

/** 삭제 팝업 */
@Composable
private fun DetailDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    CommonDialog(
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        title = "삭제 확인",
        message = "정말로 삭제하시겠습니까?",
        confirmText = "삭제",
        dismissText = "취소"
    )
}

/** 본문 영역 */
@Composable
private fun DetailBody(
    data: DiaryDetail,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(top = 16.dp, bottom = 100.dp))
    ) {
        // --- Header ---
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy. MM. dd.") }
            val createdDateText = remember(data.date) { data.date.format(dateFormatter) }

            // 작성일과 수정일이 다를 경우 '수정일'을 표시하기 위한 플래그
            val hasBeenUpdated = remember(data.date, data.updateDate) {
                data.date != data.updateDate
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = createdDateText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.width(10.dp))

                if (hasBeenUpdated) {
                    Text(
                        text = "(최종 수정일 : ${data.updateDate})",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Red,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = data.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Body ---
        Text(
            text = data.body,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .heightIn(max = 400.dp)
                .verticalScroll(rememberScrollState()),
            style = MaterialTheme.typography.bodyLarge,
            color = Black.copy(alpha = 0.9f),
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Tags ---
        if (data.tags.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                HorizontalDivider(
                    modifier = Modifier.padding(bottom = 24.dp),
                    thickness = DividerDefaults.Thickness,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                Text(
                    text = "관련 태그",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    data.tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            contentColor = Black
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.bodyMedium,
                                color = White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
