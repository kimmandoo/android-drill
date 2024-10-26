package com.kimmandoo.domain.usecase.file

import java.io.InputStream

interface GetInputStreamUseCase {
    suspend operator fun invoke(contentUri: String): Result<InputStream>
}