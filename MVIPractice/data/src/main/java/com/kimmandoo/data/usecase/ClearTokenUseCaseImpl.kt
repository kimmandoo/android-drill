package com.kimmandoo.data.usecase

import com.kimmandoo.data.datastore.UserDataStore
import com.kimmandoo.domain.usecase.login.ClearTokenUseCase
import com.kimmandoo.domain.usecase.login.GetTokenUseCase
import javax.inject.Inject


class ClearTokenUseCaseImpl @Inject constructor(
    private val userDataStore: UserDataStore,
) : ClearTokenUseCase {
    override suspend fun invoke(): Result<Unit> = runCatching{
        userDataStore.clear()
    }
}