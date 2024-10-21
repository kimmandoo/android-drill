package com.kimmandoo.presentation.login

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kimmandoo.presentation.main.MainActivity
import com.kimmandoo.presentation.component.MGButton
import com.kimmandoo.presentation.component.MGTextField
import com.kimmandoo.presentation.theme.MVIPracticeTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun LoginScreen(
    // 이렇게 해버리면 NavHost에서 관리되는 생명주기가 달라서 viewModel이 소멸되지않는 현상이 발생함
    // composable이 있는지 확인해서 viewModel 생명주기를 조절해주는 방식을 사용해야됨 -> 쉽지않다 .
    // 이걸 hilt-compose가 대신해줌
    viewModel: LoginViewModel = hiltViewModel(), // navHost에서 composable 상태에따라 적절히 생명주기 조절가능
    onNavigateToSignUpScreen: () -> Unit,
) {    // 상태를 가져와야됨
    val state = viewModel.collectAsState().value // orbit.compose에 달려있는 것
    val context = LocalContext.current
    viewModel.collectSideEffect { sideEffect ->// sideeffect 수행하는 곳
        when (sideEffect) {
            is LoginSideEffect.ShowToast -> { // SealedClass로 수행한 이유
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is LoginSideEffect.NavigateToMainActivity -> {
                context.startActivity(
                    Intent(
                        context, MainActivity::class.java
                    ).apply {
                        flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //  다시 LoginActivity로 돌아오지 않게
                    }
                )
            }
        }
    }

    // 얘는 ViewModel을 들고있을 거라서 preview에 따로 나오지 않음
    LoginScreen(
        id = state.id,
        password = state.password,
        onIdChanged = viewModel::onIdChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onLoginButtonClicked = viewModel::onLoginClick,
        onNavigateToSignUpScreen = onNavigateToSignUpScreen
    )

}

@Composable
private fun LoginScreen(
    id: String,
    password: String,
    onIdChanged: (String) -> Unit, // TextField를 쓸것이기 때문에
    onPasswordChanged: (String) -> Unit,
    onLoginButtonClicked: () -> Unit,
    onNavigateToSignUpScreen: () -> Unit,
) {
    Surface {
        // Surface로 배경 깔기
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Connected",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    "your favorite SNS",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Column(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .background(MaterialTheme.colorScheme.background) // background 기본색상
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    modifier = Modifier.padding(top = 36.dp),
                    text = "Log in",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "id",
                    style = MaterialTheme.typography.labelLarge
                )
                TextField(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    value = id,
                    onValueChange = onIdChanged,
                    colors = TextFieldDefaults.colors(
                        disabledIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                // password
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "password",
                    style = MaterialTheme.typography.labelLarge
                )
                // Custom TextField
                MGTextField(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    value = password,
                    onValueChange = onPasswordChanged,
                    visualTranformation = PasswordVisualTransformation()
                )
                MGButton(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    text = "로그인하기",
                    onClick = onLoginButtonClicked
                )
                Spacer(modifier = Modifier.weight(1f)) // 공간을 일단 주기
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                        .clickable {
                            onNavigateToSignUpScreen()
                        }
                ) {
                    Text(
                        text = "아직 계정이 없으신가요?",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    MVIPracticeTheme {
        LoginScreen(
            id = "this is",
            password = "mandoo",
            onIdChanged = {},
            onPasswordChanged = {},
            onLoginButtonClicked = {},
            onNavigateToSignUpScreen = {}
        )
    }
}