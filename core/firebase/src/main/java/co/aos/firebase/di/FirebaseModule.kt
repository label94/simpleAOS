package co.aos.firebase.di

import co.aos.firebase.auth.FirebaseAuthDataSource
import co.aos.firebase.firestore.FirebaseFirestoreDataSource
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
}