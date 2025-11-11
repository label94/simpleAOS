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
     *
     * @param mood 1..5
     *  1: 매우 지치고 힘든 날 (위로, 안전, 감정 정리 중심)
     *  2: 답답하고 불안한 날 (원인 탐색, 작은 안도감)
     *  3: 평범한 날 (담담한 회고, 작은 포인트 발견)
     *  4: 대체로 좋은 날 (감사, 성취, 기억 남기기)
     *  5: 정말 좋은 날 (기쁜 순간, 의미 부여, 미래 기대)
     *
     * @param hint 관심 키워드 (ex: "직장", "연애", "가족", "건강", "성장")
     *
     * @return "프롬프트 — 예시 시작 문장" 형식의 5줄
     */
    suspend fun generateDailyPrompts(
        mood: Int?,
        hint: String?
    ): List<String> = withContext(Dispatchers.IO) {

        // 1) 프롬프트 구성
        val prompt = buildString {
            appendLine("당신은 감정 일기 작성을 돕는 코치입니다.")
            appendLine("사용자가 오늘 일기를 자연스럽게 시작할 수 있도록, '질문 + 예시 시작 문장' 형태의 영감을 제안하세요.")
            appendLine()
            appendLine("요구사항:")
            appendLine("1. 한국어로 작성할 것.")
            appendLine("2. 총 5개의 제안을 만들 것.")
            appendLine("3. 각 줄은 하나의 프롬프트와 그에 맞는 예시 시작 문장을 함께 포함할 것.")
            appendLine("4. 형식은 다음을 따르세요 (번호/불릿 금지):")
            appendLine("   프롬프트 문장 — 예시 시작 문장")
            appendLine("   예) 오늘 나를 가장 편안하게 만들어 준 순간은 무엇이었을까? — 퇴근 후 집에 들어와 신발을 벗던 그 짧은 순간부터 떠올려본다.")
            appendLine("5. 프롬프트는 거창하지 않고, 실제 일상에서 있었을 법한 구체적인 장면을 떠올리게 해야 한다.")
            appendLine("6. 예시 시작 문장은 1문장으로, 사용자가 바로 이어서 쓸 수 있게 자연스럽게 시작할 것.")
            appendLine()

            appendLine("기분 점수에 따른 톤 조정 규칙:")
            appendLine("- mood=1: 매우 지치고 힘든 날입니다. 위로, 안전감, 감정 풀어내기, '괜찮다'는 메시지를 중심으로 부드럽게 유도하세요.")
            appendLine("- mood=2: 답답하고 불안한 날입니다. 불편했던 순간의 원인을 가볍게 돌아보고, 작은 안도/회복 가능성을 느끼게 해주세요.")
            appendLine("- mood=3: 평범한 날입니다. 오늘 있었던 일들 중 인상 깊었던 장면을 담담하게 정리하고, 작은 의미를 찾도록 유도하세요.")
            appendLine("- mood=4: 대체로 좋은 날입니다. 감사했던 일, 뿌듯했던 순간, 성장이나 성취를 기록하고 남기도록 도와주세요.")
            appendLine("- mood=5: 아주 기분 좋은 날입니다. 가장 행복했던 순간을 자세히 남기고, 왜 특별했는지, 앞으로의 기대까지 써보도록 유도하세요.")
            appendLine("mood 값이 주어지면 반드시 해당 톤을 반영하세요. 주어지지 않은 경우에는 3점(평범한 날)에 맞춰 자연스럽게 작성합니다.")
            appendLine("현재 나의 기분 점수는 $mood 입니다.")
            appendLine()

            if (!hint.isNullOrBlank()) {
                appendLine("관심 키워드: $hint")
                appendLine("가능하다면 이 키워드를 일부 프롬프트와 예시에 자연스럽게 녹여 주세요.")
                appendLine("하지만 키워드를 어색하게 반복하지는 마세요.")
            }

            appendLine()
            appendLine("출력 형식 추가 조건:")
            appendLine("- 설명 문장, 메타 설명, 규칙 나열, 불릿포인트, 번호를 포함하지 마세요.")
            appendLine("- 오직 5줄만 출력하세요.")
            appendLine("- 각 줄은 '프롬프트 — 예시 시작 문장' 형식을 지키세요.")
        }

        // 4) 호출 (실제 메서드명/구조는 SDK ref 문서 기준으로 맞출 것)
        val response = model.generateContent(prompt)

        // 5) 응답 텍스트 파싱 (개행 기준으로 자르기)
        val raw = response.text ?: return@withContext emptyList()
        raw
            .lines()
            .map { it.trim().trimStart('-', '•', '*') }
            .filter { it.isNotBlank() }
            .take(5)
    }
}