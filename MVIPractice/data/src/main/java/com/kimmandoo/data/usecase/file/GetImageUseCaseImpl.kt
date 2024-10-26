package com.kimmandoo.data.usecase.file

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.kimmandoo.domain.model.Image
import com.kimmandoo.domain.usecase.file.GetImageUseCase
import javax.inject.Inject

class GetImageUseCaseImpl @Inject constructor(
    private val context: Context
) : GetImageUseCase {
    override fun invoke(contentUri: String): Image? {
        val uri = Uri.parse(contentUri)
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE
        )
        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )

        return cursor?.use { c->
            c.moveToNext() // 커서를 한칸 내려서 정보 가져오기
            val idIdx = c.getColumnIndexOrThrow(projection[0])
            val nameIdx = c.getColumnIndexOrThrow(projection[1])
            val sizeIdx = c.getColumnIndexOrThrow(projection[2])
            val typeIdx =  c.getColumnIndexOrThrow(projection[3])
            // idx를 알아야 커서가 컬럼에 대한 값을 가져올 수 있다
            val id = c.getLong(idIdx)
            val name = c.getString(nameIdx)
            val size = c.getLong(sizeIdx)
            val type = c.getString(typeIdx)
            Image(contentUri, name, size, type)
        }
    }
}