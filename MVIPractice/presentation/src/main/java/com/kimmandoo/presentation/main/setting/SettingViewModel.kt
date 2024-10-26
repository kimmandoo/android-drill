package com.kimmandoo.presentation.main.setting

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.kimmandoo.domain.usecase.login.ClearTokenUseCase
import com.kimmandoo.domain.usecase.main.setting.GetMyUserUseCase
import com.kimmandoo.domain.usecase.main.setting.SetProfileImageUseCase
import com.kimmandoo.domain.usecase.main.setting.UpdateMyUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

private const val TAG = "SettingViewModel"

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val clearTokenUseCase: ClearTokenUseCase,
    private val getMyUserUseCase: GetMyUserUseCase,
    private val updateMyUserUseCase: UpdateMyUserUseCase,
    private val setProfileImageUseCase: SetProfileImageUseCase,
) : ViewModel(), ContainerHost<SettingState, SettingSideEffect> {
    override val container: Container<SettingState, SettingSideEffect> = container(
        initialState = SettingState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(SettingSideEffect.ShowToast(throwable.message ?: "알 수 없는 에러"))
                }
            }
        }
    )

    init { // init 블록의 위치가 놀랍게도 container 초기화 시점에 영향을 끼친다...
        load() // 뷰모델이 인스턴스에 올라가자마자 호출될 것
    }

    private fun load() = intent {
        val user = getMyUserUseCase().getOrThrow()
        Log.d(TAG, "load: $user")
        reduce {
            state.copy(
                profileImageUrl = user.profileImageUrl,
                username = user.username,
            )
        }
    }

    fun onLogoutClick() = intent {
        clearTokenUseCase().getOrThrow() // 예외가 발생하면 SideEffect에서 받을 것
        postSideEffect(SettingSideEffect.NavigateToLogin)
    }

    fun onUsernameChange(username: String) = intent {
        updateMyUserUseCase(username).getOrThrow()
        // getOrThrow를 사용해서, 문제가 생기면 exceptionHandler가 처리한다
        load() // SettingState에 적용이 되어야하기 때문에 load를 한번 더 불러주자
    }

    fun onImageChange(contentUri: Uri?) = intent {
        // 이미지만 변경하고 싶은거지 그 내부는 전혀 알고 싶지 않다
        Log.d(TAG, "onImageChange: clicked")
        setProfileImageUseCase(contentUri = contentUri.toString())
        load()
    }
}

@Immutable
data class SettingState(
    val profileImageUrl: String? = null,
    val username: String = "",
)

sealed interface SettingSideEffect {
    data class ShowToast(val message: String) : SettingSideEffect
    data object NavigateToLogin : SettingSideEffect
}