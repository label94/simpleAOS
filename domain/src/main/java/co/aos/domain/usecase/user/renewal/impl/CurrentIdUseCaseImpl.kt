package co.aos.domain.usecase.user.renewal.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.renewal.CurrentIdUseCase
import javax.inject.Inject

class CurrentIdUseCaseImpl @Inject constructor(
    private val repository: UserRepository
): CurrentIdUseCase {
    override suspend fun invoke(): String? = repository.currentId()
}