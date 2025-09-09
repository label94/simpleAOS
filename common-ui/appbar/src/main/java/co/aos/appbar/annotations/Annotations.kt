package co.aos.appbar.annotations

/**
 * 어노테이션 관련 정의
 *  참고 소스 : https://github.com/onebone/compose-collapsing-toolbar
 * */

@RequiresOptIn(
    message = "This is an experimental API of compose-collapsing-toolbar. Any declarations with " +
            "the annotation might be removed or changed in some way without any notice.",
    level = RequiresOptIn.Level.WARNING
)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalToolbarApi