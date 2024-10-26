package com.kimmandoo.data.usecase.file

import android.util.Log
import com.kimmandoo.data.ktor.ImageService
import com.kimmandoo.domain.model.Image
import com.kimmandoo.domain.usecase.file.GetInputStreamUseCase
import com.kimmandoo.domain.usecase.file.UploadImageUseCase
import kotlinx.io.files.FileNotFoundException
import javax.inject.Inject

class UploadImageUseCaseImpl @Inject constructor(
    private val imageService: ImageService,
    private val getInputStreamUseCase: GetInputStreamUseCase
): UploadImageUseCase {
    override suspend fun invoke(image: Image): Result<String> = runCatching {
        val byteArray = getInputStreamUseCase(image.uri).getOrElse {
            Log.e("UploadImageUseCase", "Failed to get InputStream: ${it.message}")
            throw it
        }.use { inputStream ->
            inputStream.readBytes()
        }

        imageService.uploadImage(image, byteArray).data?.filePath
            ?: throw IllegalStateException("File path is null")
    }
}