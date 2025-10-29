package co.aos.data.datasource.impl

import co.aos.data.datasource.FirebaseUserDataSource
import co.aos.domain.model.User
import co.aos.firebase.auth.FirebaseAuthDataSource
import co.aos.firebase.firestore.FirebaseFirestoreDataSource
import co.aos.firebase.model.UserDto
import javax.inject.Inject
import androidx.core.net.toUri
import co.aos.firebase.model.DiaryEntryDto
import java.time.LocalDate

/**
 * Firebase User 연동 관련 DataSource
 * */
class FirebaseUserDataSourceImpl @Inject constructor(
    private val authDS: FirebaseAuthDataSource,
    private val fsDS: FirebaseFirestoreDataSource
): FirebaseUserDataSource {
    override suspend fun createUser(id: String, password: String): String =
        authDS.signUp(id, password)

    override suspend fun login(id: String, password: String): String =
        authDS.signIn(id, password)

    override suspend fun currentUid(): String? = authDS.currentUid()

    override fun signOut() {
        authDS.signOut()
    }

    override suspend fun deleteCurrentUserIfAny() {
        authDS.deleteCurrentUserIfAny()
    }

    override suspend fun upsertUser(data: User) {
        val dto = UserDto(
            uid = data.uid,
            id = data.id,
            nickName = data.nickName,
            localProfileImgCode = data.localProfileImgCode
        )
        fsDS.upsertUserTransaction(dto)
    }

    override suspend fun fetchUser(uid: String): User? {
        val dto = fsDS.fetchUser(uid) ?: return null
        return User(
            uid = dto.uid,
            id = dto.id,
            nickName = dto.nickName,
            localProfileImgCode = dto.localProfileImgCode
        )
    }

    override suspend fun touchLastLogin(uid: String) {
        fsDS.updateLastLogin(uid)
    }

    override suspend fun isNicknameAvailable(nickname: String) =
        fsDS.isNicknameAvailable(nickname.lowercase())

    override suspend fun reserveNickname(uid: String, nickname: String) {
        fsDS.reserveNickName(uid, nickname.lowercase())
    }

    override suspend fun releaseNickname(nickname: String) {
        fsDS.releaseNickName(nickname.lowercase())
    }

    override suspend fun isIdAvailable(id: String): Boolean =
        fsDS.isIdAvailable(id)

    override suspend fun reserveId(uid: String, id: String) {
        fsDS.reserveId(uid, id)
    }

    override suspend fun releaseId(id: String) {
        fsDS.releaseId(id)
    }

    override suspend fun updateProfileTransaction(
        uid: String,
        newNick: String,
        newLocalProfileImgCode: Int
    ) {
        fsDS.updateProfileTransaction(uid, newNick, newLocalProfileImgCode)
    }

    override suspend fun currentId(): String? {
        return authDS.currentId()
    }

    override suspend fun reauthenticate(id: String, password: String) {
        authDS.reauthenticate(id, password)
    }

    override suspend fun updatePassword(newPassword: String) {
        authDS.updatePassword(newPassword)
    }

    override suspend fun addDiaryEntry(
        uid: String,
        title: String,
        body: String,
        tags: List<String>,
        date: LocalDate,
        pinned: Boolean
    ): String {
        return fsDS.addDiaryEntry(uid, title, body, tags, date, pinned)
    }

    override suspend fun updateDiaryEntry(
        uid: String,
        entryId: String,
        update: Map<String, Any?>
    ) {
        fsDS.updateDiaryEntry(uid, entryId, update)
    }

    override suspend fun deleteDiaryEntry(uid: String, entryId: String) {
        fsDS.deleteDiaryEntry(uid, entryId)
    }

    override suspend fun getDiaryEntry(
        uid: String,
        entryId: String
    ): DiaryEntryDto? {
        return fsDS.getDiaryEntry(uid, entryId)
    }

    override suspend fun recentDiaryEntries(
        uid: String,
        pageSize: Int,
        cursorId: String?
    ): Pair<List<Pair<String, DiaryEntryDto>>, String?> {
        return fsDS.recentDiaryEntries(uid, pageSize, cursorId)
    }

    override suspend fun entriesByDate(
        uid: String,
        day: LocalDate
    ): List<Pair<String, DiaryEntryDto>> {
        return fsDS.entriesByDate(uid, day)
    }

    override suspend fun searchDiaryEntries(
        uid: String,
        from: LocalDate?,
        to: LocalDate?,
        pinnedOnly: Boolean,
        pageSize: Int,
        cursorId: String?
    ): Pair<List<Pair<String, DiaryEntryDto>>, String?> {
        return fsDS.searchDiaryEntries(uid, from, to, pinnedOnly, pageSize, cursorId)
    }

    override suspend fun upsertDailyMood(
        uid: String,
        day: LocalDate,
        mood: Int
    ) {
        fsDS.upsertDailyMood(uid, day, mood)
    }

    override suspend fun loadWeeklyMood(
        uid: String,
        end: LocalDate
    ): Map<String, Int?> {
        return fsDS.loadWeeklyMood(uid, end)
    }

    override suspend fun setDiaryPinned(
        uid: String,
        entryId: String,
        pinned: Boolean
    ) {
        fsDS.setDiaryPinned(uid, entryId, pinned)
    }
}