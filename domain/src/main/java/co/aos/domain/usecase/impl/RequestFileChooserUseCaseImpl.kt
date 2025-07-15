package co.aos.domain.usecase.impl

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import co.aos.domain.repository.FileChooserRepository
import co.aos.domain.usecase.RequestFileChooserUseCase
import javax.inject.Inject

/**
 * 파입 업로드 관련 유스케이스 구현체
 * */
class RequestFileChooserUseCaseImpl @Inject constructor(
    private val fileChooserRepository: FileChooserRepository
): RequestFileChooserUseCase {

    /** repository 와 연동 */
    override fun onShowFileChooser(
        filePathCallback: ValueCallback<Array<out Uri?>?>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ) {
        fileChooserRepository.onShowFileChooser(filePathCallback, fileChooserParams)
    }

    /** 파일 선택 후 처리 */
    override fun onFileChosen(uris: List<Uri?>?) {
        fileChooserRepository.onFileChosen(uris)
    }
}