package co.aos.firebase.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * firebase Auth 관련 DataSource
 * */
class FirebaseAuthDataSource(
    private val auth: FirebaseAuth
) {
    /** 로그인 */
    suspend fun signUp(id: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(id, password).await()
        return result.user?.uid ?: ""
    }
}