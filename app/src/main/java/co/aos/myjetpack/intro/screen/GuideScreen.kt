package co.aos.myjetpack.intro.screen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.aos.myjetpack.R
import co.aos.myjetpack.intro.model.GuidePermissionData
import co.aos.myjetpack.ui.theme.LightSeaGreen
import co.aos.myjetpack.ui.theme.White
import co.aos.myutils.log.LogUtil

/**
 * 접근권한 안내 화면
 * */
@SuppressLint("UnusedCrossfadeTargetStateParameter")
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun GuideScreen(
    onComplete: () -> Unit,
    onBack: () -> Unit,
    requiredPermissionList: List<GuidePermissionData>,
    optionalPermissionList: List<GuidePermissionData>
) {
    // 디스플레이 사이즈
    val activity = LocalActivity.current
    val windowSizeClass = if (activity != null) {
        calculateWindowSizeClass(activity = activity)
    } else {
        null
    }

    // Back 버튼 처리
    BackHandler {
        onBack.invoke()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.guide_permission_screen_title))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .navigationBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, bottom = 10.dp, top = 10.dp)
                    .height(56.dp)
            ) {
                Text(text = stringResource(R.string.btn_ok), fontSize = 18.sp)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(White)
        ) {
            if (windowSizeClass == null) {
                // null 인 경우 기본 디스플레이 사용
                PhoneDisplay(
                    requiredPermissionList = requiredPermissionList,
                    optionalPermissionList = optionalPermissionList
                )
            } else {
                Crossfade(
                    targetState = windowSizeClass.widthSizeClass,
                ) { sizeClass ->
                    when(sizeClass) {
                        WindowWidthSizeClass.Compact -> {
                            // 기본 휴대폰 레이아웃
                            PhoneDisplay(
                                requiredPermissionList = requiredPermissionList,
                                optionalPermissionList = optionalPermissionList
                            )
                        }
                        WindowWidthSizeClass.Medium -> {
                            // 태블릿 레이아웃(세로) or 폴더블 접힌 상태 대응
                            TabletDisplay(
                                requiredPermissionList = requiredPermissionList,
                                optionalPermissionList = optionalPermissionList
                            )
                        }
                        WindowWidthSizeClass.Expanded -> {
                            // 대형 레이아웃, or 폴더블 펼친 상태 대응
                            ExpandedDisplay(
                                requiredPermissionList = requiredPermissionList,
                                optionalPermissionList = optionalPermissionList
                            )
                        }
                    }
                }
            }
        }
    }
}

/** 휴대폰 레이아웃 */
@Composable
fun PhoneDisplay(
    requiredPermissionList: List<GuidePermissionData>,
    optionalPermissionList: List<GuidePermissionData>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.guide_permission_screen_contents),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 필수 접근 권한
        PermissionSectionTitle(
            title = stringResource(R.string.guide_permission_screen_require_title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        requiredPermissionList.forEach {
            PermissionItem(
                icon = it.icon,
                permissionName = it.name,
                description = it.description,
                isOptional = it.isOptional
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 선택 접근 권한
        PermissionSectionTitle(
            title = stringResource(R.string.guide_permission_screen_optional_title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        optionalPermissionList.forEach {
            PermissionItem(
                icon = it.icon,
                permissionName = it.name,
                description = it.description,
                isOptional = it.isOptional
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 설명 영역
        Text(
            text = stringResource(R.string.guide_permission_screen_contents2),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = stringResource(R.string.guide_permission_screen_contents3),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** 태블릿 레이아웃 */
@Composable
fun TabletDisplay(
    requiredPermissionList: List<GuidePermissionData>,
    optionalPermissionList: List<GuidePermissionData>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.guide_permission_screen_contents),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 24.dp)
                .align(Alignment.CenterHorizontally)
        )

        // 필수 접근 권한
        PermissionSectionTitle(
            title = stringResource(R.string.guide_permission_screen_require_title),
            modifier = Modifier.padding(horizontal = 8.dp) // 좌우 패딩 추가
        )
        requiredPermissionList.forEach { permissionData ->
            PermissionItemCard(
                icon = permissionData.icon,
                permissionName = permissionData.name,
                description = permissionData.description,
                isOptional = permissionData.isOptional
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 선택적 접근 권한
        PermissionSectionTitle(
            title = stringResource(R.string.guide_permission_screen_optional_title),
            modifier = Modifier.padding(horizontal = 8.dp) // 좌우 패딩 추가
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            //columns = GridCells.Adaptive(minSize = 300.dp), // 각 아이템의 최소 너비를 지정하여, 자동으로 열 개수 조절
            contentPadding = PaddingValues(0.dp), // grid 자체 패딩은 0
            verticalArrangement = Arrangement.spacedBy(16.dp), // 아이템 간 수직 간격
            horizontalArrangement = Arrangement.spacedBy(16.dp), // 아이템 간 수평 간격
            modifier = Modifier.weight(1f)
        ) {
            items(optionalPermissionList) { permissionData ->
                PermissionItemCard(
                    icon = permissionData.icon,
                    permissionName = permissionData.name,
                    description = permissionData.description,
                    isOptional = permissionData.isOptional
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.guide_permission_screen_contents2),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(R.string.guide_permission_screen_contents3),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

/** 대형 레이아웃 */
@Composable
fun ExpandedDisplay(
    requiredPermissionList: List<GuidePermissionData>,
    optionalPermissionList: List<GuidePermissionData>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.guide_permission_screen_contents),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .align(Alignment.CenterHorizontally)
        )

        // 필수 접근 권한
        PermissionSectionTitle(
            title = stringResource(R.string.guide_permission_screen_require_title),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        requiredPermissionList.forEach { permissionData ->
            PermissionItemCard(
                icon = permissionData.icon,
                permissionName = permissionData.name,
                description = permissionData.description,
                isOptional = permissionData.isOptional
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 선택적 접근 권한
        PermissionSectionTitle(
            title = stringResource(R.string.guide_permission_screen_optional_title),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 280.dp), // 각 아이템의 최소 너비를 지정하여, 자동으로 열 개수 조절
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(optionalPermissionList) { permissionData ->
                PermissionItemCard(
                    icon = permissionData.icon,
                    permissionName = permissionData.name,
                    description = permissionData.description,
                    isOptional = permissionData.isOptional
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 하단 안내 문구
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.guide_permission_screen_contents_expanded_contents),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            Text(
                text = stringResource(R.string.guide_permission_screen_contents_expanded_contents2),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            Text(
                text = stringResource(R.string.guide_permission_screen_contents3),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** 권한 제목 공통 UI */
@Composable
fun PermissionSectionTitle(title: String, modifier: Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        modifier = modifier
    )
    Spacer(modifier = Modifier.height(2.dp))

    HorizontalDivider(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp))
}

/** 공통 권한 내용 UI */
@Composable
fun PermissionItem(
    icon: ImageVector,
    permissionName: String,
    description: String,
    isOptional: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = permissionName,
            tint = if (isOptional) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = permissionName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (isOptional) Icons.Filled.Info else Icons.Filled.CheckCircle,
                    contentDescription = if (isOptional) "선택" else "필수",
                    tint = if (isOptional) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** 공통 권한 내용 UI (Card 형태) */
@Composable
fun PermissionItemCard(
    icon: ImageVector,
    permissionName: String,
    description: String,
    isOptional: Boolean,
    modifier: Modifier = Modifier
) {
    // 각 권한 항목을 Card로 감싸서 시각적 구분 강화
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
       PermissionItem(
           icon = icon,
           permissionName = permissionName,
           description = description,
           isOptional = isOptional,
           modifier = Modifier.padding(16.dp)
       )
    }
}