package co.aos.firebase.firestore

import co.aos.firebase.FirebaseCollection
import co.aos.firebase.FirebaseFireStoreKey
import co.aos.firebase.model.UserDto
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
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
}