package co.aos.firebase.firestore

import co.aos.firebase.FirebaseCollection
import co.aos.firebase.FirebaseFireStoreKey
import co.aos.firebase.model.DiaryEntryDto
import co.aos.firebase.model.UserDto
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/**
 * Firebase Firestore 관련 DataSource
 * */
class FirebaseFirestoreDataSource @Inject constructor (
    private val db: FirebaseFirestore
) {
    /** users 컬렉션에서 uid에 맞는 doc 문서 객체 반환 */
    private fun users(uid: String) =
        db.collection(FirebaseCollection.USER_COLLECTION.value).document(uid)

    /** usernames 컬렉션에서 lower에 맞는 doc 문서 객체 반환 */
    private fun usernameRef(lower: String) =
        db.collection(FirebaseCollection.USER_NAMES_COLLECTION.value).document(lower)

    /** user id 관리를 위한 컬렉션 */
    private fun userIdsRef(id: String) =
        db.collection(FirebaseCollection.USER_EMAILS_COLLECTION.value).document(id)

    /** diary 관련 컬렉션 */
    private fun diaryCol(uid: String) =
        users(uid).collection(FirebaseCollection.USER_DIARY_ENTRIES_COLLECTION.value)

    /** diary 컨텐츠가 저장 된 영역(doc) */
    private fun diaryDoc(uid: String, entryId: String) = diaryCol(uid).document(entryId)

    /** 무드 컨텐츠 관련 doc */
    private fun moodDailyDoc(uid: String, yyyyMmDd: String) =
        users(uid).collection(FirebaseCollection.USER_MOOD_DAILY_COLLECTION.value).document(yyyyMmDd)

    /** 타임 스탬프 */
    private fun tsOf(day: LocalDate): Timestamp =
        Timestamp(day.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond, 0)

    /** 사용자 데이터 삽입 및 업데이트 */
    suspend fun upsertUserTransaction(dto: UserDto) {
        db.runTransaction { tr ->
            val ref = users(dto.uid)
            val snap = tr.get(ref)
            if (snap.exists()) {
                // 업데이트
                tr.update(
                    ref, mapOf(
                        FirebaseFireStoreKey.UsersCollectionKey.ID.key to dto.id,
                        FirebaseFireStoreKey.UsersCollectionKey.NICK_NAME.key to dto.nickName,
                        FirebaseFireStoreKey.UsersCollectionKey.LOCAL_PROFILE_IMG_CODE.key to dto.localProfileImgCode,
                        FirebaseFireStoreKey.UsersCollectionKey.UPDATED_AT.key to FieldValue.serverTimestamp()
                    )
                )
            } else {
                // 삽입
                tr.set(
                    ref,
                    hashMapOf(
                        FirebaseFireStoreKey.UsersCollectionKey.UID.key to dto.uid,
                        FirebaseFireStoreKey.UsersCollectionKey.ID.key to dto.id,
                        FirebaseFireStoreKey.UsersCollectionKey.NICK_NAME.key to dto.nickName,
                        FirebaseFireStoreKey.UsersCollectionKey.LOCAL_PROFILE_IMG_CODE.key to dto.localProfileImgCode,
                        FirebaseFireStoreKey.UsersCollectionKey.CREATED_AT.key to FieldValue.serverTimestamp(),
                        FirebaseFireStoreKey.UsersCollectionKey.UPDATED_AT.key to FieldValue.serverTimestamp()
                    ), SetOptions.merge()
                )
            }
            null
        }.await()
    }

    /** 마지막 로그인 시간 업데이트 */
    suspend fun updateLastLogin(uid: String) {
        users(uid).update(FirebaseFireStoreKey.UsersCollectionKey.LAST_LOGIN_AT.key, FieldValue.serverTimestamp()).await()
    }

    /** 유저 정보 가져오기 */
    suspend fun fetchUser(uid: String): UserDto? =
        users(uid).get().await().takeIf { it.exists() }?.toObject(UserDto::class.java)

    /** 닉네임 유효성 체크 */
    suspend fun isNicknameAvailable(nicknameLower: String): Boolean =
        !usernameRef(nicknameLower).get().await().exists()

    /** 닉네임 예약하기 */
    suspend fun reserveNickName(uid: String, nicknameLower: String) {
        db.runTransaction { tr ->
            val ref = usernameRef(nicknameLower)
            val snap = tr.get(ref)
            if (snap.exists()) error("NICKNAME_TAKEN")
            tr.set(
                ref,
                mapOf(
                    FirebaseFireStoreKey.UsersCollectionKey.UID.key to uid
                )
            )
            null
        }.await()
    }

    /** 닉네임 삭제 */
    suspend fun releaseNickName(nicknameLower: String) {
        usernameRef(nicknameLower).delete().await()
    }

    /** id 유효성 검사 */
    suspend fun isIdAvailable(id: String) : Boolean =
        !userIdsRef(id).get().await().exists()

    /** id 관리를 위한 별도의 컬렉션에 저장 */
    suspend fun reserveId(uid: String, id: String) {
        db.runTransaction { tr ->
            val ref = userIdsRef(id)
            val snap = tr.get(ref)
            if (snap.exists()) error("ID_TAKEN")
            tr.set(
                ref,
                mapOf(
                    FirebaseFireStoreKey.UsersCollectionKey.UID.key to uid
                )
            )
            null
        }.await()
    }

    /** id 컬렉션 제거 */
    suspend fun releaseId(id: String) {
        userIdsRef(id).delete().await()
    }

    /** 프로필 정보 업데이트 */
    suspend fun updateProfileTransaction(
        uid: String,
        newNick: String,
        newLocalProfileImgCode: Int
    ) {
        val newNickKey = newNick.trim().lowercase()
        val now = System.currentTimeMillis()

        db.runTransaction { txn ->
            val userRef = users(uid)
            val userSnap = txn.get(userRef)
            if (!userSnap.exists()) throw IllegalStateException("NOT_EXIST_USER")

            val oldNick = userSnap.getString(FirebaseFireStoreKey.UsersCollectionKey.NICK_NAME.key) ?: ""
            val oldNickKey = oldNick.trim().lowercase()

            if (newNickKey != oldNickKey && newNickKey.isNotEmpty()) {
                val newNickRef = usernameRef(newNickKey)
                val newNickSnap = txn.get(newNickRef)

                if (newNickSnap.exists() && newNickSnap.getString(FirebaseFireStoreKey.UsersCollectionKey.UID.key) != uid) {
                    throw IllegalStateException("NICKNAME_TAKEN")
                }

                txn.set(
                    newNickRef,
                    mapOf(
                        FirebaseFireStoreKey.UsersCollectionKey.UID.key to uid,
                        FirebaseFireStoreKey.UsersCollectionKey.UPDATED_AT.key to now
                    )
                )

                // 기존 닉네임이 존재하는 경우 삭제
                if (oldNickKey.isNotEmpty()) {
                    val oldRef = usernameRef(oldNickKey)
                    val oldSnap = txn.get(oldRef)
                    if (oldSnap.exists() && oldSnap.getString(FirebaseFireStoreKey.UsersCollectionKey.UID.key) == uid) {
                        txn.delete(oldRef)
                    }
                }
            }

            txn.update(
                userRef,
                mapOf(
                    FirebaseFireStoreKey.UsersCollectionKey.NICK_NAME.key to newNick,
                    FirebaseFireStoreKey.UsersCollectionKey.LOCAL_PROFILE_IMG_CODE.key to newLocalProfileImgCode,
                    FirebaseFireStoreKey.UsersCollectionKey.UPDATED_AT.key to now
                )
            )
        }.await()
    }

    /** diary 생성 */
    suspend fun addDiaryEntry(
        uid: String,
        title: String,
        body: String,
        tags: List<String>,
        date: LocalDate,
        pinned: Boolean
    ): String {
        val ref = diaryCol(uid).document()
        val data = mapOf(
            FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_TITLE.key to title,
            FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_BODY.key to body,
            FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_TAGS.key to tags,
            FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_DATE.key to tsOf(date),
            FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_PINNED.key to pinned,
            FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_UPDATED_AT.key to FieldValue.serverTimestamp()
        )
        ref.set(data).await()
        return ref.id
    }

    /** diary 수정 */
    suspend fun updateDiaryEntry(
        uid: String,
        entryId: String,
        update: Map<String, Any?>
    ) {
        val patch = mutableMapOf<String, Any>(
            FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_UPDATED_AT.key to FieldValue.serverTimestamp()
        )
        update[FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_TITLE.key]?.let {
            patch[FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_TITLE.key] = it
        }
        update[FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_BODY.key]?.let {
            patch[FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_BODY.key] = it
        }
        update[FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_TAGS.key]?.let {
            patch[FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_TAGS.key] = it as List<*>
        }
        update[FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_PINNED.key]?.let {
            patch[FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_PINNED.key] = it as Boolean
        }
        update[FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_DATE.key]?.let {
            patch[FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_DATE.key] = tsOf(it as LocalDate)
        }
        diaryDoc(uid, entryId).update(patch).await()
    }

    /** diary 삭제 */
    suspend fun deleteDiaryEntry(uid: String, entryId: String) {
        diaryDoc(uid, entryId).delete().await()
    }

    /** 특정 diary 조회 */
    suspend fun getDiaryEntry(uid: String, entryId: String): DiaryEntryDto? {
        val snap = diaryDoc(uid, entryId).get().await()
        return if (snap.exists()) snap.toObject(DiaryEntryDto::class.java) else null
    }

    /** 최근 diary(커서 기반) */
    suspend fun recentDiaryEntries(
        uid: String,
        pageSize: Int,
        cursorId: String?
    ): Pair<List<Pair<String, DiaryEntryDto>>, String?> {
        var q: Query = diaryCol(uid)
            .orderBy(FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_DATE.key, Query.Direction.DESCENDING)
            .limit(pageSize.toLong())

        if (cursorId != null) {
            val cur = diaryDoc(uid, cursorId).get().await()
            q = q.startAfter(cur)
        }

        val snap = q.get().await()
        val list = snap.documents.mapNotNull { d ->
            d.toObject(DiaryEntryDto::class.java)?.let { d.id to it }
        }

        val next = snap.documents.lastOrNull()?.id
        return list to next
    }

    /** 날짜 별 조회 */
    suspend fun entriesByDate(
        uid: String,
        day: LocalDate
    ): List<Pair<String, DiaryEntryDto>> {
        val start = tsOf(day)
        val end = tsOf(day.plusDays(1))
        val snap = diaryCol(uid)
            .whereGreaterThanOrEqualTo(FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_DATE.key, start)
            .whereLessThan(FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_DATE.key, end)
            .orderBy(FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_DATE.key, Query.Direction.DESCENDING)
            .get().await()
        return snap.documents.mapNotNull { d ->
            d.toObject(DiaryEntryDto::class.java)?.let { d.id to it }
        }
    }

    /** 간이 검색(서버 범위: 날짜/핀, 나머지(태그/텍스트)는 클라 필터) */
    suspend fun searchDiaryEntries(
        uid: String,
        from: LocalDate?,
        to: LocalDate?,
        pinnedOnly: Boolean,
        pageSize: Int,
        cursorId: String?
    ): Pair<List<Pair<String, DiaryEntryDto>>, String?> {
        var q: Query = diaryCol(uid)
            .orderBy(FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_DATE.key, Query.Direction.DESCENDING)
            .limit(pageSize.toLong())

        if (from != null) {
            q = q.whereGreaterThanOrEqualTo(FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_DATE.key, tsOf(from))
        }

        if (to != null) {
            q = q.whereLessThan(FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_DATE.key, tsOf(to.plusDays(1)))
        }

        if (pinnedOnly) {
            q = q.whereEqualTo(FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_PINNED.key, true)
        }

        if (cursorId != null) {
            val cur = diaryDoc(uid, cursorId).get().await()
            q = q.startAfter(cur)
        }

        val snap = q.get().await()
        val list = snap.documents.mapNotNull { d ->
            d.toObject(DiaryEntryDto::class.java)?.let { d.id to it }
        }
        val next = snap.documents.lastOrNull()?.id
        return list to next
    }

    /** 무드 일일 upsert */
    suspend fun upsertDailyMood(
        uid: String,
        day: LocalDate,
        mood: Int
    ) {
        moodDailyDoc(uid, day.toString())
            .set(
                mapOf(
                    FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_MOOD.key to mood,
                    FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_UPDATED_AT.key to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            ).await()
    }

    /** 주간 무드 로드 (7개 docId whereIn) */
    suspend fun loadWeeklyMood(
        uid: String,
        end: LocalDate
    ): Map<String, Int?> {
        val days = (0..6).map { end.minusDays((6 - it).toLong()) }
        val ids = days.map { it.toString() } // yyyy-MM-dd
        val snap = users(uid).collection(FirebaseCollection.USER_MOOD_DAILY_COLLECTION.value)
            .whereIn(FieldPath.documentId(), ids).get().await()
        return snap.documents.associate { it.id to (it.getLong(FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_MOOD.key)?.toInt()) }
    }

    /** 다이어리 핀 토글 업데이트 */
    suspend fun setDiaryPinned(
        uid: String,
        entryId: String,
        pinned: Boolean
    ) {
        diaryDoc(uid, entryId).update(
            mapOf(
                FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_PINNED.key to pinned,
                FirebaseFireStoreKey.DiaryEntriesCollectionKey.D_UPDATED_AT.key to FieldValue.serverTimestamp()
            )
        ).await()
    }
}