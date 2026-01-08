package co.aos.sample

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/** 참고 할 테스트 오브젝트 */
data class TestItem(
    val id: Int,
    val name: String
)

/**
 * Compose 성능 망치는 코드 패턴 TOP 10
 * */

/**
 * =======================================================
 * 1. Composable 안에서 매번 새 List/Map/객체 생성
 * - 매 recomposition 될때마다 새 인스턴스가 생성
 * - 그 객체가 자식 파라미터로 내려가면 자식 skip이 깨짐(불 필요 recomposition)
 * => 성능 상 느려지는 이슈 발생 가능성이 높음!!
 * */

/** 나쁜 예 */
@Composable
private fun Screen() {
    // 매 recomposition 마다 새인스턴스가 만들어짐
    val tabs = listOf("Home", "Search", "My") // 매번 새 리스트
}

/** 개선 예 */
@Composable
private fun Screen1() {
    // remember 사용하여 인스턴스 유지
    val tabs = remember { listOf("Home", "Search", "My") }
}

/**
 * =======================================================
 * 2. "매번 새 람다" 생성해서 자식 recomposition/skip 깨짐
 * - 예를들어 onClick = {...} 가 매 recomposition 마다 새 함수 객체가 될 수 있음
 * - 특히 리스트 아이템에서는 큰 비용 발생
 * => 위 2가지 이유로 성능 상 느려지는 현상 발생 가능성이 높음!!
 * */


/** 나쁜 예 */
@Composable
private fun ItemRow(item: TestItem, onSelect: (Int) -> Unit) {
    Button(
        onClick = {
            onSelect.invoke(item.id)
        }
    ) {
        Text(item.name)
    }
}

/** 좋은 예1 */
@Composable
private fun ItemRow2(item: TestItem, onSelect: (Int) -> Unit) {
    // remember를 사용하여 고정
    val click = remember(item.id, onSelect) { { onSelect.invoke(item.id) } }
    Button(
        onClick = click
    ) {
        Text(item.name)
    }
}

/** 좋은 예2 */
@Composable
private fun TestListScreen(
    items: List<TestItem>,
    onSelect: (Int) -> Unit
) {
    // 상위에서 안정적으로 람다 연동(권장)
    LazyColumn {
        items(items, key = { it.id }) { item ->
            ItemRow3(
                item = item,
                onClick = { onSelect(item.id) }
            )
        }
    }
}

@Composable
private fun ItemRow3(item: TestItem, onClick: () -> Unit) {
    Button(onClick = onClick) { Text(item.name) }
}

/**
 * =======================================================
 * 3. LazyColumn 에서 key 미지정
 * - 아이템 삽입/삭제/정렬 변경 시 Compose가 "누가 누군지" 못 맞추는 현상이 발생
 * - 상태 꼬임 문제
 * - 재바인딩 증폭
 * - 스크롤/포커스/애니메이션도 깨짐
 * => 위 이유로 성능 상 느려짐!
 * */

/** 나쁜 예 */
@Composable
private fun TestLazyColumn(items: List<TestItem>) {
    // key 미 지정
    LazyColumn {
        items(items = items) { item ->

        }
    }
}

/** 좋은 예 */
@Composable
private fun TestLazyColumn2(items: List<TestItem>) {
    // key 지정
    // 이렇게 되면 id 값에 맞는 항목만 업데이트 시 해당 영역만 recomposition 됨
    LazyColumn {
        items(items, key = { it.id }) { item ->

        }
    }
}

/**
 * =======================================================
 * 4. 큰 State 를 화면 최상단에서 통째로 읽어서 전체가 매번 recomposition
 * - uiState를 Screen 최상단에서 읽고 그 값이 조금이라도 바뀌어도 Screen 아래 전부 재실행 될 수 있음
 * => 위 이유로 성능 상 문제 발생 가능성 높음!
 * */

/** 나쁜 예 */
@Composable
private fun TestHomeScreen(testSampleVM: TestSampleVM) {
    val uiState by testSampleVM.uiState.collectAsState()

    // 문제 : 섹션 단위가 아닌 통째로 state를 넘겨서 사용한 case
    // Header(uiState)
    // Content(uiState)
    // Footer(uiState)
}

/** 좋은 예 */
@Composable
private fun TestHomeScreen2(testSampleVM: TestSampleVM) {
    val uiState by testSampleVM.uiState.collectAsState()
    val name = uiState.name

    // 섹션에 필요한 상태 값만 전달!
    // Header(name)
}

/**
 * =======================================================
 * 5. Composable에서 매번 정렬/필터링/포맷팅 같은 “비싼 계산”
 * - recomposition 은 생각보다 자주 일어남 -> 매번 계산하면 바로 렉 발생 유발
 * => 성능 상 이슈 발생!
 * */

/** 나쁜 예 */
@Composable
private fun TestSearchScreen(items: List<TestItem>, query: String) {
    val filtered = items.filter { it.name.contains(query) }.sortedBy { it.name }
}

/** 좋은 예1 */
@Composable
private fun TestSearchScreen2(items: List<TestItem>, query: String) {
    // remember 사용
    val filtered by remember(items, query) {
        mutableStateOf(
            items.filter { it.name.contains(query) }.sortedBy { it.name }
        )
    }
}

/** 좋은 예2
 * derivedStateOf
 * - 컴포즈에서 어떤 State들로부터 계산되는 파생값(derived value)을 만들 때 사용
 * - 파생 값 계산을 필요한 때만 하게 해서 비용을 줄이는 목적
 * - 파생 값이 실제로 변경되지 않으면 불필요한 recomposition 업데이트를 막는 목적
 *
 * => 안에 있는 블록은 그냥 매번 실행되는 것이 아닌, 블록 내부에서 어떤 State를 읽었는지(의존성)를 Compose가 추적
 * => 그 의존 State가 변경되면 "파생 값 다시 계산", 결과가 이전과 같으면 그대로 상태 유지
 * */
@Composable
private fun TestSearchScreen3(items: List<TestItem>, query: String) {
    // derivedStateOf 사용(파생 상태 최적화)
    val filtered by remember {
        // 읽는 State 가 변경 될 때만 계산하고, 계산결과가 같으면 그대로 유지하는 State를 만든다.
        // 단, 복잡한 계산 경우에만 권장 사용 / remember 없이 사용을 하면 recomposition 마다 State 객체가 새로 만들어 질 수 있음!
        derivedStateOf {
            items.filter { it.name.contains(query) }.sortedBy { it.name }
        }
    }
}

/**
 * =======================================================
 * 6. 리스트 아이템에서 remember 키를 잘못 써서 상태 재사용/누수/꼬임
 * - 아이템 재정렬되면 기억하던 State가 "다른 아이템"에 붙어버림
 * */

/** 나쁜 예 */
@Composable
private fun TestSimpleLazyColumn(list: List<TestItem>) {
    LazyColumn {
        items(list) { item ->
            var expanded by remember { mutableStateOf(false) } // key 없음
        }
    }
}

/** 좋은 예 */
@Composable
private fun TestSimpleLazyColumn2(list: List<TestItem>) {
    LazyColumn {
        items(list, key = { it.id }) { item ->
            // 아이템 identity를 key로 묶어서 관리
            var expended by remember(item.id) { mutableStateOf(false) }
        }
    }
}

/**
 * =======================================================
 * 7. LaunchedEffect(Unit) 남발 / 잘못된 key로 무한 재실행
 * - key가 자주 변경이 되면 coroutine이 취소/재시작 반복 -> 네트워크, DB, 애니메이션 과다 실핼
 * => 성능 상 느려지는 이슈 발생
 * */

/** 나쁜 예 */
@Composable
private fun TestLaunchedScreen(data: TestItem) {
    LaunchedEffect(data) {
        // data 객체가 매번 새로 만들어지면 계속 실행
    }
}

/** 좋은 예 */
@Composable
private fun TestLaunchedScreen2(id: String) {
    LaunchedEffect(id) {
        // 안정적인 key만 사용
    }
}

/**
 * =======================================================
 * 8. collectAsState()를 리스트 아이템마다 직접 호출 (특히 같은 Flow)
 * - 아이템마다 collector가 생김 -> 불 필요한 구독/취소/재구성 비용 증가
 * */

/** 나쁜 예 */
@Composable
private fun TestCollectListScreen(vm: TestSampleVM) {
    LazyColumn {
        items(5) { index ->
            val state by vm.uiState.collectAsState() // 아이템 마다 구독...
        }
    }
}

/** 좋은 예 */
@Composable
private fun TestCollectListScreen2(vm: TestSampleVM) {
    // 상위에서 한번 collect -> 필요한 값만 전달
    val state by vm.uiState.collectAsState()
    LazyColumn {
        items(5, key = { state.name!! }) { item ->

        }
    }
}

/**
 * =======================================================
 * 9. 불안정(unstable)한 모델/컬렉션을 그대로 내려보내기
 * - MutableList, 내부 var 많은 객체 등은 Compose가 “안정적”으로 판단 못함
 * - 스킵 포기 -> 자식까지 연쇄 recomposition 발생
 * */

/** 나쁜 예 */
data class TestUiState2(
    var title: String, // var
    var list: MutableList<TestItem> // mutable
)

/** 좋은 예
 * 상태 갱신은 copy()로 새 인스턴스 생성하는 방식이 Compose랑 궁합이 좋음!
 * */
@Immutable
data class TestUiState3(
    val title: String,
    val list: List<TestItem>
)

/**
 * =======================================================
 * 10. 렌더 비용 큰 Modifier/레이어 효과를 리스트 전체에 남발
 * - recomposition이 아니라 "그리는 비용"이 많이 발생
 * */

/** 나쁜 예 */
@Composable
private fun TestRowItem() {
    // 리스트 내부에서 직접 사용 하면 리스트 아이템마다 clip + shadow + graphicsLayer로 렌더링
    Modifier
        .clip(RoundedCornerShape(16.dp))
        .shadow(12.dp)
        .graphicsLayer { alpha = 0.99f }
}

/** 좋은 예 */
@Composable
private fun TestRowItem2() {
    // 꼭 필요한 곳에서만 사용(예 : 카드 한 겹만)
    // 중첩 줄이기(Card 컴포넌트 활용)
    // clip/shadow 최소화 + 단순한 shape 사용
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(8.dp)
    ) { }
}


