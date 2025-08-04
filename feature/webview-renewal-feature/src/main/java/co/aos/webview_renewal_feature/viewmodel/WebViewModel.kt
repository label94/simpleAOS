package co.aos.webview_renewal_feature.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.base.utils.openBrowser
import co.aos.domain.usecase.RequestFileChooserUseCase
import co.aos.myutils.log.LogUtil
import co.aos.webview_renewal_feature.consts.WebConstants
import co.aos.webview_renewal_feature.js.JsEvent
import co.aos.webview_renewal_feature.state.WebViewContract
import co.aos.webview_renewal_feature.utils.WebUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * 웹뷰 뷰모델
 * */
@HiltViewModel
class WebViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val requestFileChooserUseCase: RequestFileChooserUseCase
): BaseViewModel<WebViewContract.Event, WebViewContract.State, WebViewContract.Effect>() {

    /**
     * 카메라 촬영 후 촬영한 이미지 업로드를 위한 Uri 경로
     * - 카메라 촬영 후 인텐트에서 전달되는 Uri 경로가 null 이기 때문에
     * - 맴버변수 형태로 저장하고 있어야 한다!
     * */
    private var cameraPhotoUri: Uri? = null

    /** 초기 상태 설정 */
    override fun createInitialState(): WebViewContract.State {
        return WebViewContract.State()
    }

    /** 이벤트 제어 */
    override fun handleEvent(event: WebViewContract.Event) {
        when(event) {
            is WebViewContract.Event.LoadWebViewUrl -> {
                updateWebViewUrlState(event.url)
            }
            is WebViewContract.Event.ShouldOverrideUrlLoading -> {
                shouldOverrideUrlLoading(event.url)
            }
            is WebViewContract.Event.WebViewReLoad -> {
                requestWebViewReload()
            }
            is WebViewContract.Event.OnBackPress -> {
                onBackPress(event.isWebViewCanGoBack, event.currentWebViewUrl)
            }
            is WebViewContract.Event.UpdateSwipeEnable -> {
                updateSwipeEnableState(event.isSwipeEnable)
            }
            is WebViewContract.Event.FileChooserResult -> {
                fileChooserResult(event.resultCode, event.intent)
            }
            is WebViewContract.Event.ReOpenFileChooser -> {
                openFileChooser(event.isGrantedCameraPermission)
            }
            is WebViewContract.Event.ShowFileChooser -> {
                showFileChooser(event.filePathCallback, event.fileChooserParams)
            }
        }
    }

    /** 웹에서 자바스크립트 인터페이스 호출 시 제어 */
    fun onJsEvent(event: JsEvent) {
        when(event) {
            is JsEvent.OnJsClose -> {
                finishActivity()
            }
            is JsEvent.OpenWebBrowser -> {
                val url = event.url
                if (url.isNotEmpty() && WebUtils.isSafeUrl(url)) {
                    viewModelScope.launch {
                        // 외부 브라우저 연동
                        context.openBrowser(url)
                    }
                }
            }
            is JsEvent.SubWebViewOpen -> {
                val url = event.url
                if (url.isNotEmpty() && WebUtils.isSafeUrl(url)) {
                    // 서브 웹 액티비티 호출
                    setEffect(WebViewContract.Effect.SubWebViewOpen(url))
                }
            }
        }
    }

    /** webViewUrl 상태 업데이트 */
    private fun updateWebViewUrlState(url: String?) {
        if (url.isNullOrEmpty()) return

        setState {
            copy(loadUrl = url)
        }
    }

    /** 뒤로가기 물리키 처리 */
    private fun onBackPress(
        isWebViewCanGoBack: Boolean,
        currentWebViewUrl: String? = null
    ) {
        if (isWebViewCanGoBack) {
            if (currentWebViewUrl == WebConstants.webServerType.url) {
                // 현재 url이 메인 URL과 동일하면, 바로 액티비티 종료
                finishActivity()
            } else {
                // 그 외 웹뷰 히스토리 백
                setEffect(WebViewContract.Effect.BackHistoryWebView)
            }
        } else {
            // 액티비티 종료
            finishActivity()
        }
    }

    /** ui에 웹뷰 리로드 하도록 요청 */
    private fun requestWebViewReload() {
        setEffect(WebViewContract.Effect.WebViewReload)
    }

    /** 웹뷰 뒤로가기 */
    private fun webViewGoBack() {
        setEffect(WebViewContract.Effect.BackHistoryWebView)
    }

    /** 액티비티 종료 */
    private fun finishActivity() {
        setEffect(WebViewContract.Effect.FinishActivity)
    }

    /** 웹뷰 pull to refresh 상태 업데이트 */
    private fun updateSwipeEnableState(isSwipeEnable: Boolean) {
        setState {
            copy(isSwipeEnable = isSwipeEnable)
        }
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

        if (!checkCameraPermission()) {
            // 카메라 권한 미 허용 시 권한 요청
            requestCameraPermission()
        } else {
            // 파일 탐색기 열기 위한 Effect 실행
            openFileChooser(true)
        }
    }

    /** 파일 탐색기 오픈 */
    private fun openFileChooser(isCameraPermission: Boolean) {
        launchFileChooser(getFileChooserIntent(isCameraPermission))
    }

    /** 파일 선택 후 처리 */
    private fun fileChooserResult(resultCode: Int, result: Intent?) {
        LogUtil.d(LogUtil.WEB_VIEW_LOG_TAG, "fileChooserResult cameraPhotoUri : $cameraPhotoUri " +
                "\n result : ${result?.toString()}")

        val uris: Array<Uri>? = when {
            resultCode != Activity.RESULT_OK -> {
                // 실패한 경우
                null
            }

            result?.clipData != null -> {
                // 다중 선택
                val clipData = result.clipData
                clipData?.let {
                    Array(clipData.itemCount) { index -> clipData.getItemAt(index).uri }
                }
            }

            result?.data != null -> {
                // 단일 선택
                result.data?.let { selectedData ->
                    arrayOf(selectedData)
                }
            }

            cameraPhotoUri != null -> {
                // 카메라 촬영 시
                cameraPhotoUri?.let { photoUri ->
                    arrayOf(photoUri)
                }
            }

            else -> {
                // 그 외
                null
            }
        }

        val uriList: List<Uri?>? = uris?.toList()
        LogUtil.i(LogUtil.WEB_VIEW_LOG_TAG, "fileChooserResult : $uriList")

        // 웹뷰에 업로드 하기 위해 유스케이스 호출
        requestFileChooserUseCase.onFileChosen(uriList)

        // 사용 후 초기화
        cameraPhotoUri = null
    }

    /**
     * 카메라 및 갤러리 탐색기 인텐트 생성
     * */
    private fun getFileChooserIntent(isGrantedCameraPermission: Boolean): Intent {
        val galleryIntent = createGalleryIntent() // 갤러리 앱 실행을 위한 인텐트

        // 카메라 앱 실행을 위한 인텐트
        // 단, 카메라 권한이 허용 될 경우에만 카메라 관련 인텐트 설정!
        val cameraIntent = if (isGrantedCameraPermission) {
            val cIntent = createCameraIntent()
            cIntent?.let { arrayOf(it) }
        } else {
            null
        }

        // 카메라 및 갤러리 앱 실행을 위한 인텐트 설정
        val chooserIntent = Intent(Intent.ACTION_CHOOSER).apply {
            putExtra(Intent.EXTRA_INTENT, galleryIntent)
            putExtra(Intent.EXTRA_TITLE, "미디어 파일 선택")

            // 카메라 권한 허용 시에만 카메라 앱 실행 표시
            if (isGrantedCameraPermission && cameraIntent != null) {
                putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntent)
            }
        }

        return chooserIntent
    }

    /** 카메라 앱 실행 인텐트 생성 */
    private fun createCameraIntent(): Intent? {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(context.packageManager) == null) return null

        // 사진 촬영 후 저장할 파일 생성(임시 파일)
        val mediaFile = try {
            // 임시 파일 생성 후 종료 후 파일을 자동 삭제 하기 위해 예약 설정
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

        // 사진 uri 경로 저장
        cameraPhotoUri = uri
        LogUtil.d(LogUtil.WEB_VIEW_LOG_TAG, "createCameraIntent uri : $uri")

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


    /** 카메라 권한 체크 */
    private fun checkCameraPermission(): Boolean {
        val hasCameraPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        return hasCameraPermission
    }

    /** 파일 탐색기 관련 effect 실행 */
    private fun launchFileChooser(intent: Intent) {
        setEffect(WebViewContract.Effect.LaunchFileChooser(intent))
    }

    /** 카메라 권한 요청 effect 실행 */
    private fun requestCameraPermission() {
        setEffect(WebViewContract.Effect.RequestCameraPermission)
    }

    /** shouldOverrideUrlLoading 이벤트 제어 */
    private fun shouldOverrideUrlLoading(url: String?) {}
}