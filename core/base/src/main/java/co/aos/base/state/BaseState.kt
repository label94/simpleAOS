package co.aos.base.state

/**
 * UI 상태
 *
 * - 현재 상태 값들을 위해 사용
 * */
interface UiState

/**
 * Effect 상태
 *
 * - ViewModel ->  UI에서 일회성으로 사용하기 위해 사용
 * */
interface UiEffect

/**
 * UI 이벤트 상태
 *
 * - UI -> ViewModel 일회성 이벤트를 위해 사용
 * */
interface UiEvent