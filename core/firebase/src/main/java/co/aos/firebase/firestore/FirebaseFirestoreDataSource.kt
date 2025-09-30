package co.aos.firebase.firestore

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Firebase Firestore 관련 DataSource
 * */
class FirebaseFirestoreDataSource(
    private val firestore: FirebaseFirestore
) {
    /** 회원 가입 */
    suspend fun joinUser(
        uid: String,
        id: String,
        nickName: String,
        imgPath: String?
    ) {
        val userMap = mapOf(
            "uid" to uid,
            "id" to id,
            "nickname" to nickName,
            "imgPath" to imgPath
        )
        firestore.collection("users").document(uid).set(userMap).await()
    }
}