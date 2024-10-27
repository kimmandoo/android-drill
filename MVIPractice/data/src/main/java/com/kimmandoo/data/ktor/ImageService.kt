package com.kimmandoo.data.ktor

import android.content.ContentResolver.MimeTypeInfo
import android.util.Log
import com.kimmandoo.data.model.CommonResponse
import com.kimmandoo.data.model.FileResponse
import com.kimmandoo.domain.model.Image
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.InternalAPI
import java.io.File
import javax.inject.Inject

private const val TAG = "ImageService"

class ImageService @Inject constructor(
    private val client: HttpClient,
) {
    @OptIn(InternalAPI::class)
    suspend fun uploadImage(imageFile: Image, file: ByteArray): CommonResponse<FileResponse> {
        Log.d(TAG, "uploadImage: ")
        return client.post("files") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        Log.d(TAG, "uploadImage: $imageFile ++ $file")
                        append("fileName", imageFile.name)
                        append("file", file, Headers.build {
                            append(HttpHeaders.ContentType, imageFile.mimeType)
                            append(HttpHeaders.ContentDisposition, "filename=\"${imageFile.name}\"")
                        })
                    },
                )
            )
            onUpload { bytesSentTotal, contentLength ->
                Log.d(TAG, "Sent $bytesSentTotal bytes from $contentLength")
            }
        }.body()
    }
}