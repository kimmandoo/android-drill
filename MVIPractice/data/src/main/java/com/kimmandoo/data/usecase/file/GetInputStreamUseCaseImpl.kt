package com.kimmandoo.data.usecase.file

import android.content.Context
import android.net.Uri
import com.kimmandoo.domain.usecase.file.GetInputStreamUseCase
import java.io.InputStream
import javax.inject.Inject

class GetInputStreamUseCaseImpl @Inject constructor(
    private val context: Context
): GetInputStreamUseCase {
    override suspend fun invoke(contentUri: String): Result<InputStream> = runCatching {
        val uri = Uri.parse(contentUri)
        context.contentResolver.openInputStream(uri) ?: throw IllegalStateException("비어있음")
    }
}