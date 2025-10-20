package co.aos.domain.di

import co.aos.domain.usecase.diary.AddDiaryUseCase
import co.aos.domain.usecase.diary.DeleteDiaryUseCase
import co.aos.domain.usecase.diary.GetDiaryByDateUseCase
import co.aos.domain.usecase.diary.GetDiaryUseCase
import co.aos.domain.usecase.diary.GetRecentDiaryUseCase
import co.aos.domain.usecase.diary.LoadWeeklyMoodUseCase
import co.aos.domain.usecase.diary.SearchDiaryUseCase
import co.aos.domain.usecase.diary.UpdateDiaryUseCase
import co.aos.domain.usecase.diary.UpsertDailyMoodUseCase
import co.aos.domain.usecase.diary.impl.AddDiaryUseCaseImpl
import co.aos.domain.usecase.diary.impl.DeleteDiaryUseCaseImpl
import co.aos.domain.usecase.diary.impl.GetDiaryByDateUseCaseImpl
import co.aos.domain.usecase.diary.impl.GetDiaryUseCaseImpl
import co.aos.domain.usecase.diary.impl.GetRecentDiaryUseCaseImpl
import co.aos.domain.usecase.diary.impl.LoadWeeklyMoodUseCaseImpl
import co.aos.domain.usecase.diary.impl.SearchDiaryUseCaseImpl
import co.aos.domain.usecase.diary.impl.UpdateDiaryUseCaseImpl
import co.aos.domain.usecase.diary.impl.UpsertDailyMoodUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** diary 관련 유스케이스 di 주입 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class DiaryDomainModule {

    /** diary 추가 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindAddDiaryUseCase(
        addDiaryUseCaseImpl: AddDiaryUseCaseImpl
    ): AddDiaryUseCase

    /** diary 삭제 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindDeleteDiaryUseCase(
        deleteDiaryUseCaseImpl: DeleteDiaryUseCaseImpl
    ): DeleteDiaryUseCase

    /** 날짜 별 조회 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindGetDiaryByDateUseCase(
        getDiaryByDateUseCaseImpl: GetDiaryByDateUseCaseImpl
    ): GetDiaryByDateUseCase

    /** 특정 diary 조회 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindGetDiaryUseCase(
        getDiaryUseCaseImpl: GetDiaryUseCaseImpl
    ): GetDiaryUseCase

    /** 최근 diary 조회 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindGetRecentDiaryUseCase(
        getRecentDiaryUseCaseImpl: GetRecentDiaryUseCaseImpl
    ): GetRecentDiaryUseCase

    /** 주간 별 조회 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindLoadWeeklyMoodUseCase(
        loadWeeklyMoodUseCaseImpl: LoadWeeklyMoodUseCaseImpl
    ): LoadWeeklyMoodUseCase

    /** 검색 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindSearchDiaryUseCase(
        searchDiaryUseCaseImpl: SearchDiaryUseCaseImpl
    ): SearchDiaryUseCase

    /** diary 수정 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindUpdateDiaryUseCase(
        updateDiaryUseCaseImpl: UpdateDiaryUseCaseImpl
    ): UpdateDiaryUseCase

    /** mood 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindUpsertDailyMoodUseCase(
        upsertDailyMoodUseCaseImpl: UpsertDailyMoodUseCaseImpl
    ): UpsertDailyMoodUseCase
}
