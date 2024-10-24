package com.kimmandoo.data.usecase.login

import com.kimmandoo.data.datastore.UserDataStore
import com.kimmandoo.domain.usecase.login.SetTokenUseCase
import javax.inject.Inject

class SetTokenUseCaseImpl @Inject constructor(
    private val userDataStore: UserDataStore
): SetTokenUseCase {
    override suspend fun invoke(token: String) {
        userDataStore.setToken(token)
    }
}