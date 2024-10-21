package com.kimmandoo.data.usecase

import com.kimmandoo.data.datastore.UserDataStore
import com.kimmandoo.domain.usecase.login.GetTokenUseCase
import javax.inject.Inject


class GetTokenUseCaseImpl @Inject constructor(
    private val userDataStore: UserDataStore,
) : GetTokenUseCase {
    override suspend fun invoke(): String? {
        return userDataStore.getToken()
    }
}