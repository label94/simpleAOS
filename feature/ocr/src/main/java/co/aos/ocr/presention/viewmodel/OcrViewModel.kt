package co.aos.ocr.presention.viewmodel

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.ObserveCameraOcrUseCase
import co.aos.ocr.presention.model.TextResultModel
import co.aos.ocr.presention.model.toPresentation
import co.aos.ocr.presention.state.OcrContract
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * OCR 화면 및 상태 제어를 위한 뷰모델
 * */
@HiltViewModel
class OcrViewModel @Inject constructor(
    private val useCase: ObserveCameraOcrUseCase,
    @ApplicationContext private val context: Context
): BaseViewModel<OcrContract.Event, OcrContract.State, OcrContract.Effect>() {

    // job 관리 용도
    private var cameraJob: Job? = null

    /** 초기 상태 설정 */
    override fun createInitialState(): OcrContract.State {
        return OcrContract.State()
    }

    /** 이벤트 처리 */
    override fun handleEvent(event: OcrContract.Event) {
        when(event) {
            is OcrContract.Event.StartCamera -> {
                // 카메라 시작
                observeCamera(event.lifecycleOwner, event.previewView)
            }
            is OcrContract.Event.StopCamera -> {
                // 카메라 인식 종료
                stopObserveCamera()
            }
        }
    }

    /** 카메라 인식 시작 */
    private fun observeCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        // 기존에 실행 되고 있는 job이 있으면 취소
        cameraJob?.cancel()

        // job 시작
        cameraJob = viewModelScope.launch {
            useCase.startObserving(context, lifecycleOwner, previewView).collect { result ->
                setState { copy(textResultModel = result.toPresentation()) }
            }
        }
    }

    /** 카메라 인식 종료 */
    private fun stopObserveCamera() {
        cameraJob?.cancel() // flow 수집 중지
        cameraJob = null

        // 빈 상태로 업데이트
        setState { copy(
            textResultModel = TextResultModel(
                text = "",
                blocks = emptyList()
            )
        ) }
    }
}