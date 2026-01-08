package co.aos.sample

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** 테스트 용도의 뷰모델 */
class TestSampleVM(): ViewModel() {
    private val _uiState = MutableStateFlow(TestSampleState.DEFAULT)
    val uiState: StateFlow<TestSampleState> = _uiState
}