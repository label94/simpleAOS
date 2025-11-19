package co.aos.firebase.auth

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
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

    /** 현재 로그인 중인 id(email) 가쟈오기 */
    suspend fun currentId(): String? = auth.currentUser?.email

    /** 계정 재인증 절차 수행 */
    suspend fun reauthenticate(id: String, password: String) {
        val user = auth.currentUser ?: throw IllegalStateException("NOT_SIGNED_IN")
        val cred = EmailAuthProvider.getCredential(id, password)
        user.reauthenticate(cred).await()
    }

    /** 패스워드 변경 */
    suspend fun updatePassword(password: String) {
        val user = auth.currentUser ?: throw IllegalStateException("NOT_SIGNED_IN")
        user.updatePassword(password).await()
    }

    /** 구글 ID Token으로 Firebase 로그인 */
    suspend fun signInWithGoogle(idToken: String): String {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        return result.user?.uid ?: error("signInWithGoogle No UID")
    }
}