package co.aos.base

/**
 * API 응답 처리를 위한 공통 함수
 *
 * - ApiResult 데이터 통신 결과에 대한 sealed class
 */
sealed class BaseApiResult<out T> {
    /**
     * API 로딩 시 처리할 부분
     *
     * - API 로딩 중일 때 표시할 UI 상태 정의
     * */
    data object Loading : BaseApiResult<Nothing>()

    /**
     * API 응답 성공 시 처리할 부분
     *
     * - API 응답 성공 일 때 처리할 UI 상태 정의
     *
     * @param response API 응답 데이터
     * */
    data class Success<out T>(val response: T) : BaseApiResult<T>()

    /**
     * API 응답 에러(error) 시 처리할 부분
     *
     * - API 응답 에러 일 때(error) 처리할 UI 상태 정의
     *
     * @param message API 응답 에러 메세지
     * */
    data class Error(val message: String) : BaseApiResult<Nothing>()

    /**
     * API 응답 에러(exception) 시 처리할 부분
     *
     * - Employ API 응답 에러 일 때(exception) 처리할 UI 상태 정의
     *
     * @param exception API 응답 에러 데이터
     * */
    data class Exception(val exception: Throwable? = null) : BaseApiResult<Nothing>()
}