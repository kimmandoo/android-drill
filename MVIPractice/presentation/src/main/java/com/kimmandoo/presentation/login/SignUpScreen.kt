package com.kimmandoo.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kimmandoo.presentation.component.MGButton
import com.kimmandoo.presentation.component.MGTextField
import com.kimmandoo.presentation.theme.MVIPracticeTheme

@Composable
fun SignUpScreen(
    id: String,
    username: String,
    password: String,
    passwordCheck: String,
    // 이 람다들은 이벤트를 위로 끌어올려줄 친구들
    onIdChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordCheckChanged: (String) -> Unit,
    onSignUpButtonClicked: () -> Unit
) {
    Surface {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(
                modifier = Modifier
                    .padding(48.dp)
                    .fillMaxWidth(),
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
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight()
            ) {
                Text(
                    modifier = Modifier.padding(top = 36.dp),
                    text = "계정생성해",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "ID",
                    style = MaterialTheme.typography.labelLarge
                )
                MGTextField(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    value = id,
                    onValueChange = onIdChanged
                )
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Username",
                    style = MaterialTheme.typography.labelLarge
                )
                MGTextField(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    value = username,
                    onValueChange = onUsernameChanged
                )
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Password",
                    style = MaterialTheme.typography.labelLarge
                )
                MGTextField(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    value = password,
                    onValueChange = onPasswordChanged
                )
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Password Check",
                    style = MaterialTheme.typography.labelLarge
                )
                MGTextField(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    value = passwordCheck,
                    onValueChange = onPasswordCheckChanged
                )
                MGButton(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    text = "Sign Up",
                    onClick = onSignUpButtonClicked
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    MVIPracticeTheme {
        SignUpScreen(
            id = "decore",
            username = "Cleo Acosta",
            password = "montes",
            passwordCheck = "delicata",
            onIdChanged = {},
            onUsernameChanged = {},
            onPasswordChanged = {},
            onPasswordCheckChanged = {},
            onSignUpButtonClicked = {})
    }
}