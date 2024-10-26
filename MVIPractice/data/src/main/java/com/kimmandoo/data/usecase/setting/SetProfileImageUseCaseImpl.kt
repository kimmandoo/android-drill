package com.kimmandoo.data.usecase.setting

import android.util.Log
import com.kimmandoo.data.ktor.ImageService
import com.kimmandoo.domain.usecase.file.GetImageUseCase
import com.kimmandoo.domain.usecase.file.UploadImageUseCase
import com.kimmandoo.domain.usecase.main.setting.GetMyUserUseCase
import com.kimmandoo.domain.usecase.main.setting.SetProfileImageUseCase
import com.kimmandoo.domain.usecase.main.setting.UpdateMyUserUseCase
import javax.inject.Inject

private const val TAG = "SetProfileImageUseCaseI"

class SetProfileImageUseCaseImpl @Inject constructor(
    private val userUseCase: GetMyUserUseCase,
    private val updateMyUserUseCase: UpdateMyUserUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val getImageUseCase: GetImageUseCase
) : SetProfileImageUseCase {
    override suspend fun invoke(contentUri: String): Result<Unit> = runCatching {
        // 1. 사진 업로드 -> 이게 좀 복잡함
        val image = getImageUseCase(contentUri) ?: throw NullPointerException()
        // 2. 업로드 된 사진 url 가져오기
        val imagePath = uploadImageUseCase(image).getOrThrow()
        // 3. 내정보 업데이트 하기
        Log.d(TAG, "invoke: $imagePath")
        updateMyUserUseCase(profileImageUrl = "http://10.0.2.2:8080/" + imagePath).getOrThrow()
    }
}