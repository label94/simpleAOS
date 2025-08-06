package co.aos.local.pref.consts

/**
 * SharedPreference 관련 상수
 * */
object SharedConstants {
    /** 저장소 이름 */
    const val SHARED_PREFERENCE_NAME = "my_preference"

    /**
     * 앱 최초 1회 실행 유무
     * - true : 최초 실행
     * - false : 최초 실행 X
     * */
    const val KEY_IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH"
}