package co.aos.widget_feature.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import co.aos.widget_feature.ui.MainDiaryWidget

/**
 * Glance 위젯은 반드시 GlanceAppWidgetReceiver 를 통해
 * 시스템(AppWidgetManager)과 연결된다.
 *
 * 이 클래스는 단순히 어떤 GlanceAppWidget 을 쓸지 반환만 한다.
 */
class MoodWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = MainDiaryWidget()
}