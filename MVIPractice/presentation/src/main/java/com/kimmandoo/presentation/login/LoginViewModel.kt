package com.kimmandoo.presentation.login

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimmandoo.domain.usecase.login.LoginUseCase
import com.kimmandoo.domain.usecase.login.SetTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

private const val TAG = "LoginViewModel"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val setTokenUseCase: SetTokenUseCase,
) : ViewModel(), ContainerHost<LoginState, LoginSideEffect> {
    // container가 상태관리를 해줄건데, 생성자체는 내가 해줘야됨
    override val container: Container<LoginState, LoginSideEffect> = container(
        initialState = LoginState(), // 초기상태 지정
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
                intent { // 에러 조차 intent로 관리할 수 있다.
                    postSideEffect(LoginSideEffect.ShowToast("예외발생 ${throwable.message}"))
                }
            }
        }
    )

    fun onLoginClick() = intent {
        // 컨테이너가 들고있는 state에서 바로 값을 뽑아올 수 있다.
        val id = state.id
        val password = state.password
        viewModelScope.launch {
//            loginUseCase(id = id, password = password).onSuccess {
//                Log.d(TAG, "onLoginClick: $it")
//                // Orbit의 좋은점
//                postSideEffect(LoginSideEffect.ShowToast("로그인 성공 $it"))
//            }.onFailure {
//
//            }
            val token = loginUseCase(id = id, password = password).getOrThrow()
            setTokenUseCase(token)
            postSideEffect(LoginSideEffect.ShowToast("로그인 성공 $token"))
            postSideEffect(LoginSideEffect.NavigateToMainActivity)
        }
    }

    fun onIdChanged(id: String) = intent {
        // state reducer를 사용해서 제어해야되는데, orbit에서 제공하는 intent라는 DSL reduce가 있다.
        reduce {
            // 여기서 state는 LoginState
            state.copy(id = id) // 리듀서가 반환한 state가 container에 들어가게되고, 상태가 변경된다.
        }

    }

    fun onPasswordChanged(password: String) = intent {
        reduce {
            state.copy(password = password)
        }
    }
}

@Immutable // Compose로 쓸때는 Immutable 붙여주면좋음
data class LoginState(
    val id: String = "",
    val password: String = "",
)

// SideEffect 관리할 인터페이스
// 에러핸들링은 side effect로 하면 좋지않겠ㄴ
sealed interface LoginSideEffect { // 상태와 관련 없는 놈들
    data class ShowToast(val message: String) : LoginSideEffect
    data object NavigateToMainActivity : LoginSideEffect
}