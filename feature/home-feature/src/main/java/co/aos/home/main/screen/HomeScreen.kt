package co.aos.home.main.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.aos.card.AnimatedCard
import co.aos.common.showSnackBarMessage
import co.aos.home.bottomsheet.MoodPickerSheet
import co.aos.home.main.screen.card.DiaryCard
import co.aos.home.main.screen.card.FilterEmptyCard
import co.aos.home.main.screen.card.RecentEmptyCard
import co.aos.home.main.screen.card.TagChipsSection
import co.aos.home.main.screen.card.TodaySummaryCard
import co.aos.home.main.screen.card.WeeklyMoodCard
import co.aos.home.main.state.HomeContract
import co.aos.home.main.viewmodel.HomeViewModel
import co.aos.home.topbar.HomeTopBar
import co.aos.loading.skeleton.ListItemSkeleton
import java.time.LocalDate

/**
 * 홈 화면
 * @param snackBarHostState : 스낵바 상태
 * @param onOpenEditor : 작성/수정 콜백(에디터로 이동)
 * @param onOpenDetail : 상세로 이동
 * @param onOpenSearch : 검색 이동
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    snackBarHostState: SnackbarHostState,
    onOpenEditor: () -> Unit,
    onOpenDetail: (String) -> Unit,
    onOpenSearch: () -> Unit,
) {
    // ui Sate
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val effectFlow = viewModel.effect
    val activity = LocalActivity.current

    // 스크롤 동작 정의: 스크롤 내릴 땐 숨기고, 올릴 땐 나타남
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // 1회성 이벤트 처리
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when(effect) {
                is HomeContract.Effect.Toast -> {
                    // 스낵바 표시
                    showSnackBarMessage(
                        snackBarHostState = snackBarHostState,
                        coroutineScope = coroutineScope,
                        message = effect.msg
                    )
                }
                is HomeContract.Effect.NavigateToEditor -> {
                    // 에디터로 이동
                    onOpenEditor.invoke()
                }
                is HomeContract.Effect.NavigateToDetail -> {
                    // 상세 이동
                    onOpenDetail.invoke(effect.id)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopBar(
                scrollBehavior = scrollBehavior,
                query = uiState.query,
                onQuery = {
                    viewModel.setEvent(HomeContract.Event.OnSearchChanged(it))
                },
                onBell = {},
                onSettings = {}
            )
        }
    ) { innerPadding ->
        HomeBody(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            uiState = uiState,
            list = rememberLazyListState(),
            onEvent = viewModel::setEvent
        )

//        LazyColumn(
//            modifier = Modifier
//                .padding(innerPadding)
//                .fillMaxSize()
//        ) {
//            items(60) { index ->
//                Text("Item $index", modifier = Modifier.padding(16.dp))
//            }
//        }
    }

    // 무드 선택 시트
    if (uiState.showMoodPicker) {
        MoodPickerSheet(
            onDismiss = {
                viewModel.setEvent(HomeContract.Event.HideMoodPicker)
            },
            onPick = {
                viewModel.setEvent(HomeContract.Event.OnMoodPicked(it))
            }
        )
    }
}

private val SectionSpacing = 16.dp

/** 홈 컨텐츠 영역 */
@Composable
private fun HomeBody(
    modifier: Modifier = Modifier,
    uiState: HomeContract.State,
    list: LazyListState,
    onEvent: (HomeContract.Event) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = list,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(SectionSpacing)
    ) {
        // 1) 오늘의 요약
        item {
            AnimatedCard {
                TodaySummaryCard(
                    todayMood = uiState.todayMood,
                    todayWritten = uiState.todayWritten,
                    streak = uiState.streak,
                    bestStreak = uiState.bestStreak,
                    onPickMood = { onEvent(HomeContract.Event.OnPickMoodClick) },
                    onWrite = { onEvent(HomeContract.Event.QuickAddText) },
                    loading = uiState.loading
                )
            }
        }

        // 2) 주간 무드
        item {
            AnimatedCard {
                WeeklyMoodCard(
                    weekly = uiState.weeklyMood,
                    streak = uiState.streak,
                    bestStreak = uiState.bestStreak,
                    loading = uiState.loading,
                    endDate = LocalDate.now()
                )
            }
        }

        // 3) 태그 칩
        item {
            AnimatedCard {
                TagChipsSection(
                    tags = listOf("직장","운동","감사","독서","산책","개발","요리"),
                    selected = uiState.selectedTags,
                    onToggle = { onEvent(HomeContract.Event.OnTagToggled(it)) },
                    onClear = { onEvent(HomeContract.Event.OnClearTagFilters) }
                )
            }
        }

        // 4) 최근 일기 (로딩/빈/목록)
        when {
            uiState.loading -> {
                // 로딩 UI 표시
                items(3) { ListItemSkeleton() }
            }
            uiState.recentEntries.isEmpty() && uiState.selectedTags.isNotEmpty() -> {
                // 필터는 있는데 결과 없을 경우 "Empty Card UI" 표시
                item {
                    AnimatedCard {
                        FilterEmptyCard(
                            selectedCount = uiState.selectedTags.size,
                            onClear = { onEvent(HomeContract.Event.OnClearTagFilters) }
                        )
                    }
                }
            }
            uiState.filteredEntries.isEmpty() || uiState.recentEntries.isEmpty() -> {
                // 전체도 비어 있을 경우
                item {
                    AnimatedCard {
                        RecentEmptyCard { onEvent(HomeContract.Event.QuickAddText) }
                    }
                }
            }
            else -> {
                // 필터에 맞는 데이터 표시
                items(uiState.filteredEntries, key = { it.id }) { e ->
                    AnimatedCard {
                        DiaryCard(
                            data = e,
                            onClick =  { onEvent(HomeContract.Event.OnEntryClick(e.id)) },
                            onTagClick = { tag -> onEvent(HomeContract.Event.OnTagFromCardClicked(tag)) }
                        )
                    }
                }
            }
        }
        item { Spacer(Modifier.height(60.dp)) }
    }
}
