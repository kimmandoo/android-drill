package com.kimmandoo.domain.usecase.writing

import com.kimmandoo.domain.model.Image

interface GetLocalImageListUseCase {
    suspend operator fun invoke(): List<Image>
}