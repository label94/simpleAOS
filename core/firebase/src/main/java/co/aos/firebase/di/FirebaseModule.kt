package co.aos.firebase.di

import co.aos.firebase.ai.FirebaseAIDataSource
import co.aos.firebase.auth.FirebaseAuthDataSource
import co.aos.firebase.firestore.FirebaseFirestoreDataSource
import com.google.firebase.Firebase
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Firebase 관련 DI
 * */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(auth: FirebaseAuth) = FirebaseAuthDataSource(auth)

    @Provides
    @Singleton
    fun provideFirebaseFirestoreDataSource(firestore: FirebaseFirestore) =
        FirebaseFirestoreDataSource(firestore)

    /**
     * GenerativeModel 을 전역 싱글톤으로 제공
     * - Firebase 콘솔에서 Firebase AI Logic + Gemini Provider 설정이 되어 있어야 한다.
     * - 여기서 모델 이름만 바꾸면 전체 AI 로직에 반영됨.
     */
    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        // 백앤드 설정
        val backend = GenerativeBackend.googleAI()

        // Firebase.ai(...) 는 Firebase SDK에서 제공하는 정적 접근 함수/익스텐션
        return Firebase.ai(backend = backend)
            .generativeModel("gemini-2.5-flash") // 필요 시 config로 분리 가능
    }

    /**
     * FirebaseAIDataSource 제공
     * - 여기서 받은 GenerativeModel을 내부에서 사용.
     */
    @Provides
    @Singleton
    fun provideFirebaseAIDataSource(
        model: GenerativeModel
    ): FirebaseAIDataSource = FirebaseAIDataSource(model)
}