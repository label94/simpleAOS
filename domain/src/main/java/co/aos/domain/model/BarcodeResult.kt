package co.aos.domain.model

import androidx.annotation.Keep

/**
 * 바코드 결과 데이터 set
 * */
@Keep
data class BarcodeResult(
    val rawValue: String
)
