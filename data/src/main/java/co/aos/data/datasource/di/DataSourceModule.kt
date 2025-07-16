package co.aos.data.datasource.di

import co.aos.data.datasource.CameraOcrDataSource
import co.aos.data.datasource.impl.CameraOcrDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DataSource di 관리 모듈
 * */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataSourceModule {

    /** OCR 관련 DataSource 주입 */
    @Binds
    @Singleton
    abstract fun bindCameraOcrDataSource(
        cameraOcrDataSourceImpl: CameraOcrDataSourceImpl
    ): CameraOcrDataSource
}