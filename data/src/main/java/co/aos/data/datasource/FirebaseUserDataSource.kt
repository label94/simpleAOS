package co.aos.data.datasource

import co.aos.domain.model.User
import co.aos.firebase.model.DiaryEntryDto
import java.time.LocalDate
import java.time.YearMonth

/**
 * firebase 연동 관련 dataSource
 * */
interface FirebaseUserDataSource {
    /** 회원가입 */
    suspend fun createUser(id: String, password: String): String

    /** 로그인 */
    suspend fun login(id: String, password: String): String

    /** 현재 로그인의 uid 가져오기 */
    suspend fun currentUid(): String?

    /** 로그아웃 */
    fun signOut()

    /** 사용자 정보 제거 */
    suspend fun deleteCurrentUserIfAny()

    /** 사용자 정보 업데이트 및 등록 */
    suspend fun upsertUser(data: User)

    /** uid에 맞는 user 정보 반환 */
    suspend fun fetchUser(uid: String): User?

    /** 마지막 로그인 시간 업데이트 */
    suspend fun touchLastLogin(uid: String)

    /** 닉네임 유효성 검증 */
    suspend fun isNicknameAvailable(nickname: String): Boolean

    /** 닉네임 예약하기 */
    suspend fun reserveNickname(uid: String, nickname: String)

    /** 닉네임 제거 */
    suspend fun releaseNickname(nickname: String)

    /** id 유효성 체크 */
    suspend fun isIdAvailable(id: String): Boolean

    /** id 컬렉션 생성 */
    suspend fun reserveId(uid: String, id: String)

    /** id 컬렉션 제거 */
    suspend fun releaseId(id: String)

    /** 프로필 정보 업데이트 */
    suspend fun updateProfileTransaction(
        uid: String,
        newNick: String,
        newLocalProfileImgCode: Int
    )

    /** 로그인 되어 있는 id(email) 정보 반환 */
    suspend fun currentId(): String?

    /** 재인증 */
    suspend fun reauthenticate(id: String, password: String)

    /** 비밀번호 변경 */
    suspend fun updatePassword(newPassword: String)

    /** diary 추가 */
    suspend fun addDiaryEntry(
        uid: String,
        title: String,
        body: String,
        tags: List<String>,
        date: LocalDate,
        pinned: Boolean
    ): String

    /** diary 수정 */
    suspend fun updateDiaryEntry(
        uid: String,
        entryId: String,
        update: Map<String, Any?>
    )

    /** diary 삭제 */
    suspend fun deleteDiaryEntry(uid: String, entryId: String)

    /** 특정 diary 조회 */
    suspend fun getDiaryEntry(uid: String, entryId: String): DiaryEntryDto?

    /** 최근 diary(커서 기반) */
    suspend fun recentDiaryEntries(
        uid: String,
        pageSize: Int,
        cursorId: String?
    ): Pair<List<Pair<String, DiaryEntryDto>>, String?>

    /** 날짜 별 조회 */
    suspend fun entriesByDate(
        uid: String,
        day: LocalDate
    ): List<Pair<String, DiaryEntryDto>>

    /** 간이 검색(서버 범위: 날짜/핀, 나머지(무드/태그/텍스트)는 클라 필터) */
    suspend fun searchDiaryEntries(
        uid: String,
        from: LocalDate?,
        to: LocalDate?,
        pinnedOnly: Boolean,
        pageSize: Int,
        cursorId: String?
    ): Pair<List<Pair<String, DiaryEntryDto>>, String?>

    /** 무드 일일 upsert */
    suspend fun upsertDailyMood(
        uid: String,
        day: LocalDate,
        mood: Int
    )

    /** 주간 무드 로드 (7개 docId whereIn) */
    suspend fun loadWeeklyMood(
        uid: String,
        end: LocalDate
    ): Map<String, Int?>

    /** 다이어리 핀 토글 업데이트 */
    suspend fun setDiaryPinned(
        uid: String,
        entryId: String,
        pinned: Boolean
    )

    /** 특정 월에 맞는 다이어리 데이터 조회 */
    suspend fun entriesByMonth(
        uid: String,
        yearMonth: YearMonth
    ): List<Pair<String, DiaryEntryDto>>

    /** 임의의 날짜가 속한 '그 달'의 다이어리 조회 */
    suspend fun entriesByMonth(
        uid: String,
        dayInMonth: LocalDate
    ): List<Pair<String, DiaryEntryDto>>
}