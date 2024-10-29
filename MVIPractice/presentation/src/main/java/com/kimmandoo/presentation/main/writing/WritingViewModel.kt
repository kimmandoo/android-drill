package com.kimmandoo.presentation.main.writing

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.kimmandoo.domain.model.Image
import com.kimmandoo.domain.usecase.writing.GetLocalImageListUseCase
import com.kimmandoo.presentation.main.setting.SettingSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

private const val TAG = "WritingViewModel"

@HiltViewModel
class WritingViewModel @Inject constructor(
    private val getLocalImageListUseCase: GetLocalImageListUseCase,
) : ViewModel(), ContainerHost<WritingState, WritingSideEffect> {
    override val container: Container<WritingState, WritingSideEffect> = container(
        initialState = WritingState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(WritingSideEffect.Toast(throwable.message ?: "알 수 없는 에러"))
                }
            }
        }
    )

    init {
        load()
    }

    private fun load() = intent {
        // 이미지를 불러온다
        val images = getLocalImageListUseCase()
        Log.d(TAG, "load: $images")
        reduce {
            state.copy(
                selectedImages = images.firstOrNull()?.let { listOf(it) } ?: emptyList(),
                images = images
            )
        }
    }

    fun onImageClick(image: Image) = intent {
        reduce {
            state.copy(
                selectedImages = if (state.selectedImages.contains(image)) {
                    state.selectedImages - image
                } else {
                    state.selectedImages + image
                }
            )
        }
    }

    @OptIn(OrbitExperimental::class)
    fun onTextChanged(input: String) = blockingIntent {
        reduce {
            state.copy(
                text = input
            )
        }
    }

    fun onPostClick() = intent {
        val writingState = state
        // selectedImage랑 text만 뽑아서 전송하면 됨
    }
}

@Immutable
data class WritingState(
    val images: List<Image> = emptyList(),
    val selectedImages: List<Image> = emptyList(),
    val text: String = "",
)

sealed interface WritingSideEffect {
    data class Toast(val message: String) : WritingSideEffect
}