package co.aos.data.repository

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import co.aos.domain.repository.FileChooserRepository
import javax.inject.Inject

/**
 * FileChooser 관련 Repository 구현체
 * */
class FileChooserRepositoryImpl @Inject constructor() : FileChooserRepository {

    /** 파일 업로드 관련 콜백 */
    private var filePathCallback: ValueCallback<Array<out Uri?>?>? = null

    /** 파일 탐색기 열기 */
    override fun onShowFileChooser(
        filePathCallback: ValueCallback<Array<out Uri?>?>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ) {
        this.filePathCallback = filePathCallback
    }

    /** 파일 선택 후 결과 처리 */
    override fun onFileChosen(uris: List<Uri?>?) {
        filePathCallback?.onReceiveValue(uris?.toTypedArray())
        filePathCallback = null
    }
}