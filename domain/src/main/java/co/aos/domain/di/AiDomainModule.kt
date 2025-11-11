package co.aos.domain.di

import co.aos.domain.usecase.ai.GetDailyPromptsUseCase
import co.aos.domain.usecase.ai.impl.GetDailyPromptsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** AI 관련 주입을 위한 모듈 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class AiDomainModule {

    /** 오늘의 영감 관련 유스케이스 di 주입 */
    @Binds
    @Singleton
    abstract fun bindGetDailyPromptsUseCase(
        getDailyPromptsUseCaseImpl: GetDailyPromptsUseCaseImpl
    ): GetDailyPromptsUseCase
}