package co.aos.webview_feature.presentation.viewmodel

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

    /**
     * 카메라 촬영 후 촬영한 이미지 업로드를 위한 Uri 경로
     * - 카메라 촬영 후 인텐트에서 전달되는 Uri 경로가 null 이기 때문에
     * - 맴버변수 형태로 저장하고 있어야 한다!
     * */
    private var cameraPhotoUri: Uri? = null

    init {
        // 초기 웹뷰 설정 이벤트 호출
        setEvent(WebViewContract.Event.InitWebViewConfig)
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
            is WebViewContract.Event.UpdateLoadWebViewUrl -> {
                // 웹뷰 URL 업데이트
                updateLoadWebViewUrl(event.url)
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
            is WebViewContract.Event.ReOpenFileChooser -> {
                // 파일 탐색기 재오픈
                openFileChooser(event.isGrantedCameraPermission)
            }
            is WebViewContract.Event.ReLoadWebUrl -> {
                // 웹뷰 url 변경 후 리로드
                reLoadWebUrl()
            }
        }
    }

    /**
     * 초기 웹뷰 설정
     * - UserAgent 설정
     * */
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

    /** 웹뷰 URL 업데이트 */
    private fun updateLoadWebViewUrl(loadWebUrl: String) {
        setState { copy(url = loadWebUrl) }
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

        if (!checkCameraPermission()) {
            // 카메라 권한 미 허용 시 권한 요청
            setEffect(WebViewContract.Effect.RequestCameraPermission)
        } else {
            // 파일 탐색기 열기 위한 Effect 실행
            openFileChooser(true)
        }
    }

    /** 파일 탐색기 오픈 */
    private fun openFileChooser(isCameraPermission: Boolean) {
        setEffect(WebViewContract.Effect.LaunchFileChooser(getFileChooserIntent(isCameraPermission)))
    }

    /** 파일 선택 후 처리 */
    private fun fileChooserResult(resultCode: Int, result: Intent?) {
        LogUtil.d(LogUtil.WEB_VIEW_LOG_TAG, "fileChooserResult createCameraIntent uri : $cameraPhotoUri \n result : ${result?.toString()}")

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
        LogUtil.d(LogUtil.WEB_VIEW_LOG_TAG, "fileChooserResult : $uriList")

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

    /** onNewIntent 호출 되었을 때 웹뷰 리로드 */
    private fun reLoadWebUrl() {
        val newUrl = currentState.url
        if (!newUrl.isNullOrEmpty()) {
            setEffect(WebViewContract.Effect.ReLoadWebViewUrl(newUrl))
        }
    }
}