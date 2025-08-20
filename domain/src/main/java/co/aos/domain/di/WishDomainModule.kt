package co.aos.domain.di

import co.aos.domain.usecase.wish.AddWishUseCase
import co.aos.domain.usecase.wish.GetWishesByUserUseCase
import co.aos.domain.usecase.wish.impl.AddWishUseCaseImpl
import co.aos.domain.usecase.wish.impl.GetWishesByUserUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Wish Domain Di 모듈
 * */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class WishDomainModule {

    @Binds
    @Singleton
    abstract fun bindAddWishUseCase(
        addWishUseCaseImpl: AddWishUseCaseImpl
    ): AddWishUseCase

    @Binds
    @Singleton
    abstract fun bindGetWishesByUserUseCase(
        getWishesByUserUseCaseImpl: GetWishesByUserUseCaseImpl
    ): GetWishesByUserUseCase
}