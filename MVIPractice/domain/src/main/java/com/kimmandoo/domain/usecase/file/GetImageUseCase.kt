package com.kimmandoo.domain.usecase.file

import com.kimmandoo.domain.model.Image

interface GetImageUseCase {
    operator fun invoke(contentUri: String): Image?
}