package com.kimmandoo.presentation.main.setting

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.kimmandoo.domain.usecase.login.ClearTokenUseCase
import com.kimmandoo.domain.usecase.main.setting.GetMyUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val clearTokenUseCase: ClearTokenUseCase,
    private val getMyUserUseCase: GetMyUserUseCase,
) : ViewModel(), ContainerHost<SettingState, SettingSideEffect> {

    init {
        load() // 뷰모델이 인스턴스에 올라가자마자 호출될 것
    }

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

    private fun load() = intent {
        val user = getMyUserUseCase().getOrThrow()
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