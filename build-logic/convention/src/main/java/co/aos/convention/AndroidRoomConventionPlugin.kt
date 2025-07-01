package co.aos.convention

import androidx.room.gradle.RoomExtension
import co.aos.convention.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Room 의존성 및 빌드 옵션 관련 convention plugin
 * */
class AndroidRoomConventionPlugin: Plugin<Project> {
    /** build login 적용 */
    override fun apply(target: Project) {
        target.run {
            // room, ksp Plugin 추가
            pluginManager.run {
                apply("androidx.room")
                apply("com.google.devtools.ksp")
            }

            // room DB schema 를 저장할 디렉토리를 설정
            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/schemas")
            }

            // room 관련 의존성 추가
            dependencies {
                add("implementation", libs.findLibrary("room.runtime").get())
                add("implementation", libs.findLibrary("room.ktx").get())
                add("ksp", libs.findLibrary("room.compiler").get())
            }
        }
    }
}