package co.aos.domain.usecase

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient

/**
 * 파일 업로드 관련 유스케이스
 * */
interface RequestFileChooserUseCase {
    /** 파일 탐색기 실행 */
    fun onShowFileChooser(
        filePathCallback: ValueCallback<Array<out Uri?>?>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    )

    /** 파일 선택 후 처리 */
    fun onFileChosen(uris: List<Uri?>?)
}