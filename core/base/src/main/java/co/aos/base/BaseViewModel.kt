package co.aos.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.aos.base.state.UiEffect
import co.aos.base.state.UiEvent
import co.aos.base.state.UiState
import co.aos.myutils.log.LogUtil
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * BaseViewModel (MVI 구조를 위해 만듬)
 *
 * - 각 화면에서 사용할 UIEvent, UIState, UIEffect를 전달 받음
 *
 * @param Event : 처리할 UI 관련 이벤트
 * @param State : UI 상태
 * @param Effect : 1회성 이벤트
 */
abstract class BaseViewModel<Event: UiEvent, State: UiState, Effect: UiEffect> : ViewModel() {
    /**
     * 초기 상태 정의
     *
     * */
    private val initialState : State by lazy {createInitialState()}

    /**
     * 초기 상태 생성
     *
     * */
    abstract fun createInitialState() : State

    /**
     * UI에서 사용할 상태 값들
     *
     * */
    private val _uiState : MutableStateFlow<State> = MutableStateFlow(initialState)
    val currentState: State get() = _uiState.value

    /**
     * UI 상태
     *
     * - 최신 데이터 값으로 유지를 위해 stateFlow 사용
     * */
    val uiState =  _uiState.asStateFlow()

    /**
     * UI 이벤트
     *
     * - UI에서 ViewModel로 전달할 이벤트
     * */
    private val _event : MutableSharedFlow<Event> = MutableSharedFlow()
    // 이벤트 전달
    val event = _event.asSharedFlow()

    /**
     * 1회성 UI 이벤트
     *
     * - ViewModel에서 UI로 전달할 1회성 이벤트
     * */
    private val _effect : Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    /**
     * 네트워크 체크
     *
     * - 네트워크 오류 공통 처리를 위해 사용하는 부분
     * */
    protected val _checkNetwork : Channel<Boolean> = Channel()
    val checkNetwork = _checkNetwork.receiveAsFlow()

    init {
        // 이벤트 결과 처리하는 부분 호출
        subscribeEvents()
    }

    /**
     * 이벤트 호출시 처리하는 함수
     *
     * - 전달 되는 이벤트를 처리하기 위하여 observer 한다.
     * */
    private fun subscribeEvents(){
        event.onEach {
            handleEvent(it)
        }.launchIn(viewModelScope)
    }

    /**
     * 이벤트 제어를 위한 함수
     *
     * - 전달 되는 이벤트에 맞게 구현할 함수
     * - 상속받은 자식 뷰모델에서 구현해서 사용해야 한다.
     * */
    abstract fun handleEvent(event : Event)

    /**
     * 처리할 이벤트 등록
     *
     * - 특정 이벤트 실행을 할 때 호출하는 함수.
     * */
    fun setEvent(event:Event){
        if (!_event.tryEmit(event)) {
            viewModelScope.launch {
                _event.emit(event)
            }
        }
    }

    /**
     * 1회성으로 실행되는 이벤트를 호출
     *
     * - 1회성 한정으로 이벤트를 실행 할 때 사용한다.
     * - Snack bar 및 팝업 표시 등에 자주 사용 된다.
     * */
    fun setEffect(effect : Effect){
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    /**
     * 네트워크 상태 등록 함수
     *
     * - 공통으로 네트워크 상태를 감지할 때 사용한다.
     * */
    fun checkNetwork(result: Boolean = true) {
        viewModelScope.launch {
            _checkNetwork.send(result)
        }
    }

    /**
     * UI 상태 업데이트
     *
     * - 특정 UI 상태 업데이트
     * */
    protected fun setState(update: State.() -> State){
        _uiState.update {
            update(it)
        }
    }

    /**
     * API 통신
     *
     * - ApiResult에 대한 네트워크 통신 처리 부분
     *
     * @param apiCall : API 통신 함수
     * @param result : API 통신 결과
     * */
    suspend inline fun <reified T> loadDataResult(apiCall: Flow<T>, crossinline result: (BaseApiResult<T>) -> Unit) {
        try {
            apiCall.onStart {
                result(BaseApiResult.Loading)
            }.onEach { response ->
                result(BaseApiResult.Success(response))
            }.catch { e ->
                result(BaseApiResult.Exception(e))
            }.collect()
        } catch (e: Exception) {
            // suspend 함수에 대한 예외처리를 위해 try _ catch 적용
            e.printStackTrace()
            LogUtil.e(LogUtil.DEFAULT_TAG, "loadDataResult() error : $e")
            result(BaseApiResult.Exception(e))
        }
    }
}