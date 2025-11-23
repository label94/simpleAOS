package co.aos.widget_feature.ui

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import co.aos.myutils.log.LogUtil
import co.aos.widget_feature.di.moodWidgetEntryPoint
import java.time.LocalDate

/** 다이러리 위젯 */
class MainDiaryWidget: GlanceAppWidget() {
    /**
     * 위젯 생성 시 초기에 단 한번만 호출
     * - 초기 데이터를 가져와 UI를 셋팅하는 함수
     * */
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val entryPoint = context.moodWidgetEntryPoint()
        val currentUid = entryPoint.getCurrentUserUseCase().invoke()?.uid
        val loadWeeklyMoodUseCase = entryPoint.loadWeeklyMoodUseCase()

        // 로그인 안 된 상태라면 로그인 유도 UI 표시
        if (currentUid == null) {
            provideContent {
                GlanceTheme {
                    NotLoginContent()
                }
            }
        } else {
            // 지난 7일 무드 불러오기
            val today = LocalDate.now()
            val weeklyMoods: List<Int?> = try {
                loadWeeklyMoodUseCase.invoke(uid = currentUid, endInclusive = today)
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.e(LogUtil.WIDGET_LOG_TAG, "weeklyMoods error : $e")
                emptyList()
            }

            LogUtil.e("TestLog", "weeklyMoods today mood : ${weeklyMoods.lastOrNull()}")

            // 위젯을 표시할 UI 정의
            provideContent {
                GlanceTheme {
                    MoodWidgetContent(
                        weeklyMoods = weeklyMoods
                    )
                }
            }
        }
    }
}