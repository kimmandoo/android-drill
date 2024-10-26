package com.kimmandoo.domain.usecase.main.setting


interface SetProfileImageUseCase {
    suspend operator fun invoke(contentUri: String): Result<Unit> // 그냥 Uri보내고 싶은데 android 의존성이 없어서 불가능
}