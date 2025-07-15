package co.aos.webview_feature.presentation.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.GetWebViewConfigUseCase
import co.aos.domain.usecase.RequestFileChooserUseCase
import co.aos.myutils.log.LogUtil
import co.aos.webview_feature.presentation.model.toPresentation
import co.aos.webview_feature.presentation.state.WebViewContract
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * 웹뷰 관련 뷰모델
 * */
@HiltViewModel
class WebViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getWebViewConfigUseCase: GetWebViewConfigUseCase,
    private val requestFileChooserUseCase: RequestFileChooserUseCase
): BaseViewModel<WebViewContract.Event, WebViewContract.State, WebViewContract.Effect>() {

    init {
        // 초기 웹뷰 설정 이벤트 호출
        setEvent(WebViewContract.Event.InitWebViewConfig)
    }

    /** 초기 웹뷰 설정 */
    private fun initWebViewSetting() {
        viewModelScope.launch {
            val config = (getWebViewConfigUseCase.invoke()).toPresentation()
            setState {
                copy(
                    webViewConfig = config
                )
            }
        }
    }

    /** should Override Loading */
    private fun shouldOverrideLoading(url: String?) {

    }

    /** 파일 탐색기 열기 */
    private fun showFileChooser(
        filePathCallback: ValueCallback<Array<out Uri?>?>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ) {
        // 파일 탐색기 관련 콜백 설정
        requestFileChooserUseCase.onShowFileChooser(
            filePathCallback = filePathCallback,
            fileChooserParams = fileChooserParams
        )

        // 파일 탐색기 열기 위한 Effect 실행
        setEffect(WebViewContract.Effect.LaunchFileChooser(getFileChooserIntent()))
    }

    /** 파일 선택 후 처리 */
    private fun fileChooserResult(resultCode: Int, result: Intent?) {
        val uris: Array<Uri>? = when {
            resultCode != Activity.RESULT_OK -> null
            result == null -> null
            result.clipData != null -> {
                // 다중 선택
                val clipData = result.clipData
                clipData?.let {
                    Array(clipData.itemCount) { index -> clipData.getItemAt(index).uri }
                }
            }
            // 단일 선택
            result.data != null -> arrayOf(result.data!!)
            else -> null
        }
        val uriList: List<Uri?>? = uris?.toList()
        LogUtil.d(LogUtil.WEB_VIEW_LOG_TAG, "fileChooserResult : $uriList")

        // 웹뷰에 업로드 하기 위해 유스케이스 호출
        requestFileChooserUseCase.onFileChosen(uriList)
    }

    /**
     * 카메라 및 갤러리 탐색기 인텐트 생성
     * */
    private fun getFileChooserIntent(): Intent {
        val cameraIntent = createCameraIntent() // 카메라 앱 실행을 위한 인텐트
        val galleryIntent = createGalleryIntent() // 갤러리 앱 실행을 위한 인텐트

        val chooserIntent = Intent(Intent.ACTION_CHOOSER).apply {
            putExtra(Intent.EXTRA_INTENT, galleryIntent)
            putExtra(Intent.EXTRA_TITLE, "미디어 파일 선택")
            putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntent?.let { arrayOf(it) })
        }

        return chooserIntent
    }

    /** 카메라 앱 실행 인텐트 생성 */
    private fun createCameraIntent(): Intent? {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(context.packageManager) == null) return null

        // 사진 촬영 후 저장할 파일 생성
        val mediaFile = try {
            createTempMediaFile().also {
                it.deleteOnExit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(LogUtil.WEB_VIEW_LOG_TAG, "createCameraIntent file error : ${e.message}")
            return null
        }

        // 저장 위치 uri 생성(프로바이더 사용)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            mediaFile
        )

        return cameraIntent.apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    /** 저장 하기 위한 사진 파일 생성 */
    private fun createTempMediaFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val prefix = "MEDIA_${timeStamp}_"
        val suffix = ".jpg"
        return File.createTempFile(prefix, suffix, context.cacheDir)
    }

    /** 갤러리 실행 인텐트 생성 */
    private fun createGalleryIntent(): Intent =
        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            type = "image/*" // 이미지만 허용
        }

    /** 초기 상태 설정 */
    override fun createInitialState(): WebViewContract.State {
        return WebViewContract.State()
    }

    /** 이벤트 처리를 위한 핸들러 */
    override fun handleEvent(event: WebViewContract.Event) {
        when(event) {
            is WebViewContract.Event.InitWebViewConfig -> {
                // 웹뷰 초기 셋팅
                initWebViewSetting()
            }
            is WebViewContract.Event.ShouldOverrideLoading -> {
                // 웹뷰 내 페이지 전환 될 때 호출
                shouldOverrideLoading(event.url)
            }
            is WebViewContract.Event.ShowFileChooser -> {
                // 웹뷰 내 파일 탐색기 오픈
                showFileChooser(event.filePathCallback, event.fileChooserParams)
            }
            is WebViewContract.Event.FileChooserResult -> {
                // 선택한 파일을 웹으로 전송
                fileChooserResult(event.resultCode, event.intent)
            }
        }
    }
}