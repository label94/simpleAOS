package co.aos.home.detail.load.screen

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import co.aos.common.showSnackBarMessage
import co.aos.home.bottombar.Routes
import co.aos.home.detail.load.state.DiaryDetailContract
import co.aos.home.detail.load.viewmodel.DiaryDetailViewModel
import co.aos.home.detail.model.DiaryDetail
import co.aos.home.topbar.DiaryDetailTopBar
import co.aos.home.utils.MoodCatalog
import co.aos.myutils.log.LogUtil
import co.aos.popup.CommonDialog
import co.aos.ui.theme.Black

/** 다이어리 상세 화면 UI */
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
            // 이전 화면 리프래시 일 경우에 호출
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
            // 상세 데이터 리프레시
            viewModel.setEvent(DiaryDetailContract.Event.Load(entryId))

            // 수정, 상세 화면 업데이트 시 이전 화면 업데이트를 위한 flag 설정 이벤트 호출
            viewModel.setEvent(DiaryDetailContract.Event.UpdateRequestRefresh(true))

            // 플래그를 다시 false로 설정(반복호출을 막기 위함)
            currentBackStackEntry.savedStateHandle[Routes.REFRESH_DETAIL] = false
        }
    }

    // 1회성 이벤트 제어
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is DiaryDetailContract.Effect.Toast -> {
                    showSnackBarMessage(
                        snackBarHostState = snackBarHostState,
                        coroutineScope = coroutineScope,
                        message = effect.msg
                    )
                }
                is DiaryDetailContract.Effect.Close -> {
                    // 닫기 버튼 눌렸을 때, 이전 화면 업데이트 Flag 유무에 따른 별도 처리

                    LogUtil.e("TestLog", "test : ${uiState.isRefresh}")

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
                    // 텍스트 공유
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
                title = uiState.entry?.title ?: "상세 화면",
                onBack = {
                    viewModel.setEvent(DiaryDetailContract.Event.OnBackClick)
                },
                onEdit = {
                    viewModel.setEvent(DiaryDetailContract.Event.OnEditClick)
                },
                onShare = {
                    viewModel.setEvent(DiaryDetailContract.Event.OnShareClick)
                },
                onDelete = {
                    viewModel.setEvent(DiaryDetailContract.Event.OnDeleteClick)
                },
            )
        },
        snackbarHost = { snackBarHostState },
        floatingActionButton = {
            // 핀 토글 FAB
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.setEvent(DiaryDetailContract.Event.OnTogglePinned)
                },
                icon = {
                    Icon(imageVector = Icons.Outlined.PushPin, contentDescription = "핀 토글")
                },
                text = {
                    Text(
                        text = if (uiState.entry?.pinned == true) "고정 해제" else "상단 고정",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    ) { innerPadding ->
        when {
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
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "페이지 오류 입니다.\n잠시후 다시 이용해주세요.", style = MaterialTheme.typography.titleMedium, color = Black)
                }
            }
            uiState.entry != null -> {
                DetailBody(
                    data = uiState.entry!!,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
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
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 날짜 + 무드
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // 날짜
            Text(
                text = data.date.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = Black
            )

            // 핀
            if (data.pinned) {
                AssistChip(
                    onClick = {},
                    label = { Text(text = "고정", style = MaterialTheme.typography.labelMedium) }
                )
            }
        }

        // 태그 칩
        if (data.tags.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                data.tags.forEach { t ->
                    AssistChip(
                        onClick = {},
                        label = { Text(text = "#$t", style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }
        }

        // 제목
        Text(
            text = data.title,
            style = MaterialTheme.typography.headlineSmall,
            color = Black
        )

        // 본문
        Text(
            text = data.body,
            style = MaterialTheme.typography.bodyLarge,
            color = Black
        )
    }
}