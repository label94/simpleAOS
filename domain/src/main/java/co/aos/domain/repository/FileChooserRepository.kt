package co.aos.domain.repository

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient

/**
 * 파일 업로드 관련 Repository
 * */
interface FileChooserRepository {

    /** 파일 탐색기 열기 */
    fun onShowFileChooser(
        filePathCallback: ValueCallback<Array<out Uri?>?>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    )

    /** 파일 선택 후 처리 */
    fun onFileChosen(uris: List<Uri?>?)
}