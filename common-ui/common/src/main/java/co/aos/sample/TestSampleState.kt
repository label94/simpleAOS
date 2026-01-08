package co.aos.sample

/** 테스트 용도의 상태 */
data class TestSampleState(
    val name: String? = null,
) {
    companion object Companion {
        val DEFAULT = TestSampleState(
            name = null
        )
    }
}
