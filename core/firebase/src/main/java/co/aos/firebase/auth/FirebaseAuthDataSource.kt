package co.aos.firebase.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * firebase Auth 관련 DataSource
 * */
class FirebaseAuthDataSource @Inject constructor (
    private val auth: FirebaseAuth
) {
    /** 회원 가입 */
    suspend fun signUp(id: String, password: String): String =
        auth.createUserWithEmailAndPassword(id, password).await().user?.uid ?: error("No UID")

    /** 로그인 */
    suspend fun signIn(id: String, password: String): String =
        auth.signInWithEmailAndPassword(id, password).await().user?.uid ?: error("No UID")

    /** 로그인한 사용자의 uid 가져오기 */
    fun currentUid(): String? = auth.currentUser?.uid

    /** 로그아웃 */
    fun signOut() = auth.signOut()

    /** 롤백용(닉네임 예약 실패 등) */
    suspend fun deleteCurrentUserIfAny() {
        val u = auth.currentUser ?: return
        u.delete().await()
    }
}