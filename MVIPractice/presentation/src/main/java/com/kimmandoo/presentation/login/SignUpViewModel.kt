@file:OptIn(OrbitExperimental::class)

package com.kimmandoo.presentation.login

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.kimmandoo.domain.usecase.login.SignUpUseCase
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

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
) : ViewModel(),
    ContainerHost<SignUpState, SignUpSideEffect> {
    override val container: Container<SignUpState, SignUpSideEffect> = container(
        // 이게 transformer
        initialState = SignUpState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
                intent {
                    postSideEffect(SignUpSideEffect.ShowToast("에러났어요 ${throwable.message.orEmpty()}"))
                }
            }
        }
    )

    fun onIdChanged(id: String) = blockingIntent { // state의 실행순서가 순간적으로 꼬여서 입력값이 꼬일 수 있다.-> blockingIntent 쓰면 됨
        // textField에 한해서 blockingIntent를 써라
        reduce {
            state.copy(id = id)
        }
    }

    fun onUsernameChanged(username: String) = blockingIntent {
        reduce {
            state.copy(username = username)
        }
    }

    fun onPasswordChanged(password: String) = blockingIntent {
        reduce {
            state.copy(password = password)
        }
    }

    fun onPasswordCheckChanged(passwordCheck: String) = blockingIntent {
        reduce {
            state.copy(passwordCheck = passwordCheck)
        }
    }

    fun onSignUpClick() = intent {
        if (state.password != state.passwordCheck) {
            // return 하면서 sideEffect로 토스트 띄우기
            postSideEffect(SignUpSideEffect.ShowToast("비밀번호가 일치하지 않습니다."))
            return@intent
        }
        val isSuccessful = signUpUseCase(state.id, state.username, state.password).getOrThrow()
        if (isSuccessful) {
            postSideEffect(SignUpSideEffect.ShowToast("회원가입 성공"))
            postSideEffect(SignUpSideEffect.NavigateToLoginScreen)
        }
    }
}

@Immutable // Compose로 쓸때는 Immutable 붙여주면좋음
data class SignUpState(
    val id: String = "",
    val username: String = "",
    val password: String = "",
    val passwordCheck: String = "",
)

// SideEffect 관리할 인터페이스
// 에러핸들링은 side effect로 하면 좋지않겠ㄴ
sealed interface SignUpSideEffect { // 상태와 관련 없는 놈들
    data class ShowToast(val message: String) : SignUpSideEffect
    data object NavigateToLoginScreen: SignUpSideEffect // object랑 data object랑 구분하기
}