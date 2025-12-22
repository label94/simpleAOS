package co.aos.algorithm

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.StringTokenizer

fun main() {
    sampleList()
}

/** readlnOrNull */
private fun testInputConsoleByReadlnOrNull() {
    // 1. 첫 번째 숫자를 입력받습니다.
    print("첫 번째 숫자를 입력하세요: ")
    val firstNumber = readlnOrNull()?.toIntOrNull()

    // 2. 두 번째 숫자를 입력받습니다.
    print("두 번째 숫자를 입력하세요: ")
    val secondNumber = readlnOrNull()?.toIntOrNull()

    println()

    println("--- 입력 결과 ---")

    // 3. 입력받은 두 숫자를 확인하고 출력합니다.
    if (firstNumber != null && secondNumber != null) {
        println("입력한 첫 번째 숫자: $firstNumber")
        println("입력한 두 번째 숫자: $secondNumber")
    } else {
        println("올바른 숫자를 입력하지 않았습니다.")
    }
}

/** bufferedReader */
private fun testInputConsoleByBufferedReader() {
    // 1. 입력을 받기 위한 BufferedReader 객체를 생성
    val br = BufferedReader(InputStreamReader(System.`in`))

    // 2. 사용자에게 입력을 요청합니다.
    print("두 개의 숫자를 공백으로 구분하여 입력하세요. (예: 10 20) : ")

    // 3. 한 줄을 통째로 읽어와서, 공백을 기준으로 문자열을 분리하는 StringTokenizer를 생성합니다.
    val st = StringTokenizer(br.readLine())

    // 4. 토큰을 순서대로 하나씩 꺼내서 숫자로 변환합니다.
    val firstNumber = st.nextToken().toIntOrNull()
    val secondNumber = st.nextToken().toIntOrNull()

    println()

    println("--- 입력 결과 ---")

    // 5. 입력받은 두 숫자를 확인하고 출력합니다.
    if (firstNumber != null && secondNumber != null) {
        println("입력한 첫 번째 숫자: $firstNumber")
        println("입력한 두 번째 숫자: $secondNumber")
    } else {
        println("올바른 숫자를 입력하지 않았습니다.")
    }

    // 자원 해제
    br.close()
}

/** 리스트 샘플 */
fun sampleList() {
    val list = listOf<Int>(1,2,5,3,4) // 읽기만 가능
    val mList = mutableListOf<Int>(100,200,300) // 추가/삭제 가능

    // 1. 사이즈
    println("리스트의 사이즈 : ${list.size}")

    // 2. 조회
    println("리스트의 0번째 값 : ${list[0]}") // 0번째 리스트의 데이터
    println("리스트의 첫번쩨 값 : ${list.first()}") // 리스트의 첫번째 데이터
    println("리스트의 마지막 값 : ${list.last()}") // 리스트의 마지막 데이터
    println("리스트의 첫번째 값 또는 해당 값이 없으면 null(firstOrNull) : ${list.firstOrNull()}") // 첫번째 데이터 없을 경우 null 반환
    println("리스트의 마지막 값 또는 해당 값이 없으면 null(lastOrNull) : ${list.lastOrNull()}") // 마지막 데이터 없을 경우 null 반환
    println("리스트의 n번쩨 데이터 값 또는 null(getOrNull) : ${list.getOrNull(5)}") // n번째 데이터 없을 경우 null 반환

    println()

    // 3. 탐색
    println("특정 데이터가 리스트에 포함되어 있는지 여부(contains) : ${list.contains(2)}")
    println("특정 데이터가 리스트에 포함되어 있는지 여부(in) : ${2 in list}")
    println("리스트의 처음부터 순서대로 n번째 데이터의 위치를 반환(indexOf) : ${list.indexOf(5)}")
    println("리스트의 뒤에서 부터 n번째 데이터의 위치를 반환(lastIndexOf) : ${list.lastIndexOf(100)}") // 찾지 못하면 -1 반환

    println()

    // 4. 조건
    println("리스트 내애 특정 조건 식 중 하나라도 존재할 경우(any) : ${list.any { it > 3 }}") // 한명 이라도 있나?
    println("리스트 내에 특정 조건 식 중 모두 만족한 경우(all) : ${list.all { it > 0 }}") // 전부 다 그래?
    println("리스트 내에 특정 조건 식 중 모두 만족하지 않을 경우(none) : ${list.none { it > 10 }}") // 아무도 안 그래?

    println()

    // 5. 변환 계열
    println("특정 규칙을 적용하여, 새로운 리스트를 생성(map) : ${list.map { it * 10 }.first()}") // 10 곱해서 첫번째 데이터 반환
    println("리스트의 데이터를 꺼내 한줄로 병합(flatMap) : ${list.flatMap { listOf(it, it) }}")

    println()

    // 6. 필터
    println("조건에 맞는 리스트를 생성(filter) : ${list.filter { it % 2 == 0 }.getOrNull(0)}")
    println("조건에 만족하지 않는 데이터를 가지고 리스트 생성(filterNot) : ${list.filterNot { it % 2 == 0 }.getOrNull(0)})")
    val testMixList = listOf(1, null, 2, 3)
    println("null 빼고 리스트 생성(filterNotNull) : ${testMixList.filterNotNull()[0]}")

    println()

    // 7. 집계
    println("리스트 데이터 총 합(sum) : ${list.sum()}")
    println("조건에 맞는 총 합을 구함(sumOf) : ${list.sumOf { it * 10 }}")
    println("리스트 데이터의 평균(average) : ${list.average()}")
    println("리스트 데이터의 최대값(max) : ${list.maxOrNull()}")
    println("리스트 데이터의 최소값(min) : ${list.minOrNull()}")
    println("리스트 데이터 갯수(count) : ${list.count()}")

    println()

    // 8. 정렬
    println("리스트 데이터를 오름차순으로 정렬(sorted) : ${list.sorted()[0]}") // 원본을 변경하지 않음, sort : 원본을 변경시키고 mutable 형태에서만 사용 가능
    println("리스트 데이터를 내림차순으로 정렬(sortedDescending) : ${list.sortedDescending()[0]}")

    println()

    // 9. 잘라내기
    println("앞에서부터 n개만 가져와 새로운 리스트 생성(take) : ${list.take(3)}")
    println("뒤에서부터 n개만 가져와 새로운 리스트 생성(takeLast) : ${list.takeLast(3)}")
    println("앞에서부터 n개만 제외하고 가져와 새로운 리스트 생성(drop) : ${list.drop(2)}")
    println("뒤에서부터 n개만 제외하고 가져와 새로운 리스트 생성(dropLast) : ${list.dropLast(2)}")
    println("n부터 m까지 가져와 새로운 리스트 생성(slice) : ${list.slice(1..3)}")
    println("n번째 부터 m번째 전 까지 가져와 새로운 리스트 생성(subList) : ${list.subList(0,3)})")

    // 10. 그룹화 / 빈도
    println("특정 조건에 맞는 그룹으로 분류하여 새로운 map 생성(groupBy) : ${list.groupBy { it % 2 == 0 }}")
    println("특정 기준에 분류 된 그룹 데이터의 빈도를 구함(groupingBy + eachCount) : ${list.groupingBy { it }.eachCount()}")
}
