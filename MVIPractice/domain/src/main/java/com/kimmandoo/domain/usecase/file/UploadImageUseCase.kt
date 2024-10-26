package com.kimmandoo.domain.usecase.file

import com.kimmandoo.domain.model.Image

interface UploadImageUseCase {
    suspend operator fun invoke(
        image: Image
    ): Result<String> // 업로드하고나서 경로 받아옴
}