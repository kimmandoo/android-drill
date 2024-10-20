package com.kimmandoo.presentation.login

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimmandoo.domain.usecase.login.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

private const val TAG = "LoginViewModel"
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel(), ContainerHost<LoginState, LoginSideEffect> {
    // container가 상태관리를 해줄건데, 생성자체는 내가 해줘야됨
    override val container: Container<LoginState, LoginSideEffect> = container(
        initialState = LoginState() // 초기상태 지정
    )

    fun onLoginClick() = intent {
        // 컨테이너가 들고있는 state에서 바로 값을 뽑아올 수 있다.
        val id = state.id
        val password = state.password
        viewModelScope.launch {
            loginUseCase(id = id, password = password).onSuccess {
                Log.d(TAG, "onLoginClick: $it")
            }.onFailure {

            }
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
    val password: String = ""
)

sealed interface LoginSideEffect // SideEffect 관리할 인터페이스