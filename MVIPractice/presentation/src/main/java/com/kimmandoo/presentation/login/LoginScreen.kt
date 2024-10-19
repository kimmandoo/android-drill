package com.kimmandoo.presentation.login

import android.widget.Space
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kimmandoo.presentation.component.MGButton
import com.kimmandoo.presentation.component.MGTextField
import com.kimmandoo.presentation.theme.MVIPracticeTheme

@Composable
fun LoginScreen(
    id: String,
    password: String,
    onIdChanged: (String) -> Unit, // TextField를 쓸것이기 때문에
    onPasswordChanged: (String) -> Unit,
    onLoginButtonClicked: () -> Unit,
    onNavigateToSignUpScreen: () -> Unit
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
                    onValueChange = onPasswordChanged
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