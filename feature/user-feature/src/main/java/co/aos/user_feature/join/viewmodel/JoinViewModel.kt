package co.aos.user_feature.join.viewmodel

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import co.aos.base.BaseViewModel
import co.aos.domain.usecase.user.legacy.IdDuplicateCheckUseCase
import co.aos.domain.usecase.user.legacy.InsertUserUseCase
import co.aos.myutils.log.LogUtil
import co.aos.user_feature.join.model.JoinUserModel
import co.aos.user_feature.join.model.toDomain
import co.aos.user_feature.join.state.JoinContract
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 회원가입 관련 뷰모델
 * */
@HiltViewModel
class JoinViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val insertUserUseCase: InsertUserUseCase,
    private val idDuplicateCheckUseCase: IdDuplicateCheckUseCase
): BaseViewModel<JoinContract.Event, JoinContract.State, JoinContract.Effect>() {

    /**
     * 카메라 촬영 후 촬영한 이미지 업로드를 위한 Uri 경로
     * - 카메라 촬영 후 인텐트에서 전달되는 Uri 경로가 null 이기 때문에
     * - 맴버변수 형태로 저장하고 있어야 한다!
     * */
    private var cameraPhotoUri: Uri? = null

    /** 초기 상태 설정 */
    override fun createInitialState(): JoinContract.State {
        return JoinContract.State()
    }

    /** 이벤트 처리 */
    override fun handleEvent(event: JoinContract.Event) {
        when(event) {
            is JoinContract.Event.InitData -> {
                initData()
            }
            is JoinContract.Event.OnJoin -> {
                joinUser()
            }
            is JoinContract.Event.OnUpdateId -> {
                setState { copy(id = event.id) }
            }
            is JoinContract.Event.OnUpdatePassword -> {
                setState { copy(password = event.password) }
            }
            is JoinContract.Event.OnUpdateNickname -> {
                setState { copy(nickname = event.nickname) }
            }
            is JoinContract.Event.OnUpdateProfileImagePath -> {
                setState { copy(profileImagePath = event.profileImagePath) }
            }
            is JoinContract.Event.OnUpdatePasswordVisible -> {
                setState { copy(passwordVisible = event.isVisible) }
            }
            is JoinContract.Event.CheckIdDuplicate -> {
                checkIdDuplicate()
            }
            is JoinContract.Event.UpdateIdDuplicate -> {
                setState { copy(isIdValid = event.isDuplicate) }
            }
            is JoinContract.Event.ClickProfileImage -> {
                // 현재 카메라 권한이 허용 된 경우에만 chooser 실행, 그 외 카메라 권한 요청
                if (currentState.isCameraPermissionGranted) {
                    createChooserIntent()
                } else {
                    setEffect(JoinContract.Effect.RequestCameraPermission)
                }
            }
            is JoinContract.Event.RequestCameraPermission -> {
                setEffect(JoinContract.Effect.RequestCameraPermission)
            }
            is JoinContract.Event.UpdateCameraPermissionGranted -> {
                setState { copy(isCameraPermissionGranted = event.isGranted) }
            }
            is JoinContract.Event.ChooserResult -> {
                fileChooserResult(event.resultCode, event.intent)
            }
        }
    }

    /** 입력 했던 데이터 초기화 */
    private fun initData() {
        setState {
            copy(
                id = "",
                password = "",
                nickname = "",
                profileImagePath = "",
                isIdValid = false,
                passwordVisible = false
            )
        }
    }

    /** 회원가입 요청 */
    private fun joinUser() {
        val id = currentState.id
        val password = currentState.password
        val nickname = currentState.nickname
        val profileImagePath = currentState.profileImagePath

        if (id.isNotEmpty() && password.isNotEmpty()) {
            // 회원 가입 요청
            val joinUserData = JoinUserModel(
                id = id,
                password = password,
                nickname = nickname,
                profileImagePath = profileImagePath
            )
            requestJoin(joinUserData)
        } else {
            if (id.isEmpty()) {
                setEffect(JoinContract.Effect.ShowSnackBar("아이디를 입력해주세요."))
                return
            }

            if (password.isEmpty()) {
                setEffect(JoinContract.Effect.ShowSnackBar("비밀번호를 입력해주세요."))
                return
            }
        }
    }

    /** 회원가입 요청 */
    private fun requestJoin(joinUserModel: JoinUserModel) {
        viewModelScope.launch {
            val result = insertUserUseCase(joinUserModel.toDomain())
            if (result) {
                setEffect(JoinContract.Effect.JoinSuccess)
            } else {
                setEffect(JoinContract.Effect.ShowSnackBar("회원가입 실패"))
            }

            // 데이터 초기화
            initData()
        }
    }

    /** id 중복 체크 */
    private fun checkIdDuplicate() {
        if (currentState.id.isEmpty()) {
            setState { copy(isIdValid = false) }
            setEffect(JoinContract.Effect.ShowSnackBar("아이디를 입력해주세요."))
            return
        }

        viewModelScope.launch {
            val result = idDuplicateCheckUseCase(currentState.id)
            if (result) {
                setState { copy(isIdValid = true) }
                setEffect(JoinContract.Effect.ShowSnackBar("사용 가능한 아이디입니다."))
            } else {
                setState { copy(isIdValid = false) }
                setEffect(JoinContract.Effect.ShowSnackBar("이미 존재하는 아이디입니다."))
            }
        }
    }

    /** intent 생성 */
    private fun createChooserIntent() {
        // 카메라 저장 용 Uri 생성
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "profile_${System.currentTimeMillis()}")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )
        cameraPhotoUri = uri // 카메라 저장소 uri 저장

        // 카메라 인텐트 생성
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }

        // 앨범 인텐트 생성
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false) // 선택 하나만 허용
            type = "image/*"
        }

        // Chooser 생성
        val chooserIntent = Intent.createChooser(galleryIntent, "프로필 이미지 선택").apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
        }

        // effect 실행
        setEffect(JoinContract.Effect.ChooserResult(chooserIntent))
    }

    /** 이미지 선택 후 결과 처리 */
    private fun fileChooserResult(resultCode: Int, result: Intent?) {
        LogUtil.d(LogUtil.JOIN_LOG_TAG, "fileChooserResult cameraPhotoUri : $cameraPhotoUri " +
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
        LogUtil.i(LogUtil.JOIN_LOG_TAG, "fileChooserResult : $uriList")

        // 상태 업데이트
        val imgPath = uriList?.get(0)?.toString() ?: ""
        setState { copy(profileImagePath = imgPath) }

        // 사용 후 초기화
        cameraPhotoUri = null
    }
}