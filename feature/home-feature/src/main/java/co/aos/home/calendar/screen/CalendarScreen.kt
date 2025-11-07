package co.aos.home.calendar.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.aos.common.noRippleClickable
import co.aos.common.showSnackBarMessage
import co.aos.domain.model.DiaryListItem
import co.aos.home.calendar.state.CalendarContract
import co.aos.home.calendar.viewmodel.CalendarViewModel
import co.aos.home.topbar.CalendarTopBar
import co.aos.ui.theme.Black
import co.aos.ui.theme.GhostWhite
import co.aos.ui.theme.Magenta
import co.aos.ui.theme.MediumBlue
import co.aos.ui.theme.Red
import co.aos.ui.theme.White
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * 상단(절반): 월 네비 + 달력 그리드
 * 하단(절반): 선택일 일기 리스트
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    onOpenDetail: (String) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    val snack = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        onBack.invoke()
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is CalendarContract.Effect.Toast -> {
                    showSnackBarMessage(
                        coroutineScope = coroutineScope,
                        snackBarHostState = snack,
                        message = effect.msg
                    )
                }
                is CalendarContract.Effect.NavigateToDetail -> {
                    onOpenDetail(effect.id)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CalendarTopBar(onBack = { onBack.invoke() })
        },
        snackbarHost = { SnackbarHost(snack) },
        containerColor = GhostWhite,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 달력 UI 영역
            CalendarView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                currentMonth = uiState.currentMonth,
                monthDays = uiState.monthDays,
                selectedDate = uiState.selectedDate,
                today = uiState.today,
                dayCount = uiState.dayCount,
                onPrevMonth = { viewModel.setEvent(CalendarContract.Event.OnPrevMonth(uiState.currentMonth)) },
                onNextMonth = { viewModel.setEvent(CalendarContract.Event.OnNextMonth(uiState.currentMonth)) },
                onDayClick = { day -> viewModel.setEvent(CalendarContract.Event.OnDayClick(day)) }
            )

            HorizontalDivider()

            // 하단 리스트 UI
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DayEntriesList(
                    items = uiState.entriesOfSelectedDay,
                    onItemClick = { id -> viewModel.setEvent(CalendarContract.Event.OnEntryClick(id)) }
                )
            }
        }
    }
}


@Composable
private fun CalendarView(
    modifier: Modifier = Modifier,
    currentMonth: YearMonth,
    monthDays: List<LocalDate>,
    selectedDate: LocalDate,
    today: LocalDate,
    dayCount: Map<LocalDate, Int>,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CalendarHeader(
            currentMonth = currentMonth,
            onPrevMonth = onPrevMonth,
            onNextMonth = onNextMonth,
        )
        WeekHeader()
        CalendarGrid(
            monthDays = monthDays,
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            today = today,
            dayCount = dayCount,
            onDayClick = onDayClick
        )
    }
}

/** 월 헤더 UI (예: 2024년 7월) */
@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onPrevMonth) {
            Icon(Icons.AutoMirrored.Filled.ArrowLeft, contentDescription = "이전 월")
        }
        Text(
            text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = "다음 월")
        }
    }
}

/** 요일 헤더(일 ~ 토) */
@Composable
private fun WeekHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val daysOfWeek = DayOfWeek.entries.toTypedArray()
        // Sunday first
        val sortedDays = daysOfWeek.sortedBy { it.value % 7 }

        for (dayOfWeek in sortedDays) {
            Text(
                modifier = Modifier.weight(1f),
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = when (dayOfWeek) {
                    DayOfWeek.SUNDAY -> Red
                    DayOfWeek.SATURDAY -> MediumBlue
                    else -> Black
                }
            )
        }
    }
}

/** 달력 6x7 그리드 UI */
@Composable
private fun CalendarGrid(
    monthDays: List<LocalDate>,
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    dayCount: Map<LocalDate, Int>,
    onDayClick: (LocalDate) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // 6주치 고정
        for (week in 0 until 6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 7일치 고정
                for (dayIndex in 0 until 7) {
                    val day = monthDays.getOrNull(week * 7 + dayIndex)
                    if (day == null) {
                        Spacer(Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        DayCell(
                            modifier = Modifier.weight(1f),
                            day = day,
                            isCurrentMonth = day.year == currentMonth.year && day.monthValue == currentMonth.monthValue,
                            isSelected = day == selectedDate,
                            isToday = day == today,
                            hasEntry = (dayCount[day] ?: 0) > 0,
                            onClick = {
                                if (day.year == currentMonth.year && day.monthValue == currentMonth.monthValue) {
                                    // 현재 달에서 표기 된 날짜들만 선택 시에 이벤트 호출
                                    onDayClick(day)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/** 단일 날짜 셀 */
@Composable
private fun DayCell(
    modifier: Modifier = Modifier,
    day: LocalDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    hasEntry: Boolean,
    onClick: () -> Unit
) {
    // 배경색 결정
    val backgroundColor = if (isSelected) Magenta else Color.Transparent
    // 텍스트 색상 결정
    val textColor = when {
        isSelected -> White
        !isCurrentMonth -> Black.copy(alpha = 0.38f)
        day.dayOfWeek == DayOfWeek.SUNDAY -> Red
        day.dayOfWeek == DayOfWeek.SATURDAY -> MediumBlue
        else -> Black
    }
    // 테두리 결정
    val borderModifier = if (isToday && !isSelected) {
        Modifier.border(1.dp, Magenta, CircleShape)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(borderModifier)
            .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
            )
        }
    }
}


/** 하단 : 선택된 날짜에 맞는 일기 리스트 */
@Composable
private fun DayEntriesList(
    items: List<DiaryListItem>,
    onItemClick: (String) -> Unit
) {
    if (items.isEmpty()) {
        Text(
            "이 날 작성한 일기가 없습니다.",
            color = Black,
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items, key = { it.id }) { item ->
            Surface(
                tonalElevation = if (item.pinned) 4.dp else 1.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .noRippleClickable { onItemClick(item.id) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tagList = item.tags
                    if (tagList.isNotEmpty()) {
                        tagList.forEach { tag ->
                            Text("#${tag} ", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Text(
                        text = item.title,
                        color = Black,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
