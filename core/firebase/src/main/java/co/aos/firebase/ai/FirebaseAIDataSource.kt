package co.aos.firebase.ai

import com.google.firebase.ai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Firebase AI 관련 DataSource
 * */
class FirebaseAIDataSource @Inject constructor(
    private val model: GenerativeModel
) {
    /**
     * 오늘의 다이어리 프롬프트 생성
     */
    suspend fun generateDailyPrompts(
        mood: Int?,
        hint: String?
    ): List<String> = withContext(Dispatchers.IO) {

        // 1) 프롬프트 구성 (간결화 버전)
        val prompt = buildString {
            appendLine("당신은 감정 일기 코치입니다. 아래 정보를 바탕으로 '질문 — 예시 문장' 형식의 일기 시작 프롬프트 4개를 한국어로 생성하세요.")
            appendLine("출력은 번호나 설명 없이, 프롬프트 4줄만 있어야 합니다.")
            appendLine()
            appendLine("--- 사용자 정보 ---")
            val effectiveMood = mood ?: 3
            appendLine("기분 점수: $effectiveMood (1=매우 나쁨, 5=매우 좋음)")
            appendLine("톤 가이드: (1:위로) (2:불안 해소) (3:차분한 회고) (4:감사/성취) (5:행복 기록)")
            if (!hint.isNullOrBlank()) {
                appendLine("관심 키워드: $hint (자연스럽게 반영)")
            }
            appendLine("--------------------")
        }

        // 2) 호출
        val response = model.generateContent(prompt)

        // 3) 응답 텍스트 파싱
        val raw = response.text ?: return@withContext emptyList()
        raw
            .lines()
            .map { it.trim().trimStart('-', '•', '*') }
            .filter { it.isNotBlank() }
            .take(4)
    }
}
