package co.aos.widget_feature.di

import android.content.Context
import co.aos.domain.usecase.diary.LoadWeeklyMoodUseCase
import co.aos.domain.usecase.diary.UpsertDailyMoodUseCase
import co.aos.domain.usecase.user.renewal.GetCurrentUserUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Glance 위젯은 Hilt 직접 @AndroidEntryPoint 를 사용하지 못하기 때문에
 * Application 에 붙은 Hilt graph 에서 필요한 의존성을 직접 꺼내오기 위한 EntryPoint 정의
 *
 * - UpsertDailyMoodUseCase : 오늘의 무드 업데이트
 * - LoadWeeklyMoodUseCase : 주간 무드 로드
 * - GetCurrentUserUseCase : 현재 유저 정보 로드(uid를 가져오기 위함)
 * */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface MoodWidgetEntryPoint {
    fun upsertDailyMoodUseCase(): UpsertDailyMoodUseCase
    fun loadWeeklyMoodUseCase(): LoadWeeklyMoodUseCase
    fun getCurrentUserUseCase(): GetCurrentUserUseCase
}

/**
 * Context 에서 위 EntryPoint 를 편하게 꺼내기 위한 helper
 * */
internal fun Context.moodWidgetEntryPoint(): MoodWidgetEntryPoint {
    return EntryPointAccessors.fromApplication(this.applicationContext, MoodWidgetEntryPoint::class.java)
}