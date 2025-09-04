package co.aos.myjetpack

import androidx.appcompat.app.AppCompatActivity

class TestScanActivity: AppCompatActivity() {

    //private lateinit var binding: ActivityTestScanBinding

//    private var rotateCheck: Thread? = null // 화면 회전 관련 처리를 위한 스레드
//    private var barcodeScanner: BarcodeScanner // 바코드 인식을 위한 스캐너
//
//    private var isDetected = false // 바코드 감지 여부
//    private lateinit var cameraController: LifecycleCameraController // 카메라의 시작과 종료를 관리하는 용도
//    private var barcode: String? = "errorBarcode" // 인식한 바코드에 대한 값
//    private var barcodeCheckList: MutableList<String> = mutableListOf() // 최근에 인식한 바코드를 저장하고 관리
//
//    private var mPreviewView: PreviewView? = null
//
//    init {
//        val options = BarcodeScannerOptions.Builder()
//            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
////            .setBarcodeFormats(Barcode.FORMAT_EAN_13)
//            .build()
//        barcodeScanner = BarcodeScanning.getClient(options)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
////        binding = ActivityTestScanBinding.inflate(layoutInflater)
////        setContentView(binding.root)
//
//        // 컨테이너 (프리뷰 + 오버레이 겹칠 용도)
//        val container = FrameLayout(this).apply {
//            layoutParams = FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.MATCH_PARENT
//            )
//        }
//        mPreviewView = PreviewView(this)
//
//        // 오버레이 뷰 (캔버스로 그릴 전용)
//        val overlay = BarcodeOverlay(this).apply {
//            layoutParams = FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.MATCH_PARENT
//            )
//        }
//
//        // 프리뷰 + 오버레이 추가 (순서 중요)
//        container.addView(mPreviewView)
//        container.addView(overlay)
//
//        setContentView(container)
//
//        startCamera2(overlay)
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private fun startCamera2(overlay: BarcodeOverlay) {
//        cameraController = LifecycleCameraController(baseContext)
//        //val previewView: PreviewView = binding.barcodePreview
//
//        val previewView = mPreviewView
//        if (previewView == null) {
//            LogUtil.e("TestLog", "previewView is null")
//            return
//        }
//
//        cameraController.setImageAnalysisAnalyzer(
//            ContextCompat.getMainExecutor(this),
//            MlKitAnalyzer(
//                listOf(barcodeScanner),
//                COORDINATE_SYSTEM_VIEW_REFERENCED,
//                ContextCompat.getMainExecutor(this)
//            ) { result ->
//                val barcodeResults = result?.getValue(barcodeScanner)
//                if (barcodeResults == null || barcodeResults.isEmpty() || barcodeResults.first() == null) {
//                    previewView.overlay.clear()
//                    previewView.setOnTouchListener { _, _ -> false }
//                    return@MlKitAnalyzer
//                }
//
//                if (!isDetected) {
//                    val barcode = barcodeResults.get(0)
//                    this.barcode = barcode.rawValue
//
//                    isDetected = true // 감지 플래그 설정
//
//                    barcode.rawValue?.let {
//                        LogUtil.e("TestLog", "barcode : $it")
//
//                        barcode?.boundingBox?.let { rect ->
//                            overlay.setBoxes(rect)
//                        }
//
//                    }
//                }
//            }
//        )
//
//        cameraController.bindToLifecycle(this)
//        previewView.controller = cameraController
//    }
//
////    @SuppressLint("ClickableViewAccessibility")
////    private fun startCamera() {
////        cameraController = LifecycleCameraController(baseContext)
////        val previewView: PreviewView = binding.barcodePreview
////
////        cameraController.setImageAnalysisAnalyzer(
////            ContextCompat.getMainExecutor(this),
////            MlKitAnalyzer(
////                listOf(barcodeScanner),
////                COORDINATE_SYSTEM_VIEW_REFERENCED,
////                ContextCompat.getMainExecutor(this)
////            ) { result ->
////                val barcodeResults = result?.getValue(barcodeScanner)
////                if (barcodeResults == null || barcodeResults.isEmpty() || barcodeResults.first() == null) {
////                    previewView.overlay.clear()
////                    previewView.setOnTouchListener { _, _ -> false }
////                    return@MlKitAnalyzer
////                }
////
////                if (!isDetected) {
////                    val barcode = barcodeResults.get(0)
////                    this.barcode = barcode.rawValue
////
////                    isDetected = true // 감지 플래그 설정
////
////                    barcode.rawValue?.let {
////                        LogUtil.e("TestLog", "barcode : $it")
////                    }
////                }
////            }
////        )
////
////        cameraController.bindToLifecycle(this)
////        //cameraController.setZoomRatio(10f)
////        previewView.controller = cameraController
////    }
//}
//
//
//class BarcodeOverlay(context: Context) : View(context) {
//
//    private val paint = Paint().apply {
//        color = Color.RED
//        style = Paint.Style.STROKE
//        strokeWidth = 6f
//    }
//
//    private var boxes: Rect? = null
//
//    fun setBoxes(newBoxes: Rect) {
//        boxes = newBoxes
//        invalidate() // 다시 그리기 요청
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        boxes?.let {
//            canvas.drawRect(it, paint)
//        }
//    }
}