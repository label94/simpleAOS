package co.aos.widget_feature.callback

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import co.aos.myutils.log.LogUtil
import co.aos.widget_feature.di.moodWidgetEntryPoint
import co.aos.widget_feature.ui.MainDiaryWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

/**
 * 무드 아이콘 클릭 시 실행되는 콜백
 *
 * - 선택된 mood(1~5)를 파라미터로 받음
 * - 현재 로그인 유저 uid 조회
 * - UpsertDailyMoodUseCase 로 오늘 날짜 무드 저장
 * - 위젯 전체 updateAll()로 새로 그림
 * */
class MoodWidgetActionCallback : ActionCallback {

    companion object {
        // Glance ActionParameters 키 (Int)
        val MOOD_PARAM_KEY = ActionParameters.Key<Int>("mood")

        // 오늘 기록된 무드를 저장하기 위한 키
        val TODAY_MOOD_KEY = intPreferencesKey("today_mood")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        LogUtil.d(LogUtil.WIDGET_LOG_TAG, "MoodWidgetActionCallback onAction()")

        val mood = parameters[MOOD_PARAM_KEY] ?: return

        val entryPoint = context.moodWidgetEntryPoint()
        val user = entryPoint.getCurrentUserUseCase().invoke()
        val upsertDailyMoodUseCase = entryPoint.upsertDailyMoodUseCase()

        // 현재 로그인 유저 uid
        val uid = try {
            user?.uid
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } ?: return

        // FireStore 업데이트 (IO)
        withContext(Dispatchers.IO) {
            val today = LocalDate.now()
            upsertDailyMoodUseCase.invoke(
                uid = uid,
                day = today,
                mood = mood
            )
        }

        // 위젯의 내부 상태(Preferences)를 업데이트합니다.
        // 이 작업이 끝나면 자동으로 UI가 다시 그려집니다.
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[TODAY_MOOD_KEY] = mood
        }

        // 위젯 리렌더 트리거 (중요!!)
        MainDiaryWidget().update(context, glanceId) // 단, 업데이트 시 위젯의 provideGlance() 함수가 호출되지는 않음!
    }
}
