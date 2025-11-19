package co.aos.googlelogin.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import co.aos.googlelogin.R
import co.aos.myutils.log.LogUtil
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * 구글 로그인 관련 DataSource
 * */
class GoogleLoginDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val credentialManager: CredentialManager
) {
    /**
     * google login 관련 token 요청
     * @return Pair(토큰, ID)
     * */
    suspend fun requestGoogleIdToken(): Pair<String, String> {
        // 1. google ID 옵션 생성
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false) // true : 이전에 승인한 계정만, false : 새 계정도 표시
            .build()

        // 2. Credential 요청 생성
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        var userPairData = Pair("", "")

        try {
            // 3. CredentialManager로 계정 선택 / 원탭 UI 호출
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential

            // 4. Google ID 토큰 크레덴셜 파싱
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val token = googleIdTokenCredential.idToken
                val displayName = googleIdTokenCredential.displayName ?: googleIdTokenCredential.id
                userPairData = Pair(token, displayName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(LogUtil.GOOGLE_LOGIN_LOG_TAG, "error : $e")
        }
        LogUtil.d(LogUtil.GOOGLE_LOGIN_LOG_TAG, "pair => $userPairData")
        return userPairData
    }
}