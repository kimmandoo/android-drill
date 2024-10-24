package com.kimmandoo.presentation.main.setting

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kimmandoo.presentation.component.MGProfileImage
import com.kimmandoo.presentation.login.LoginActivity
import com.kimmandoo.presentation.theme.MVIPracticeTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val state = viewModel.collectAsState().value
    val context = LocalContext.current
    viewModel.collectSideEffect {
        when (it) {
            is SettingSideEffect.NavigateToLogin -> {
                context.startActivity(Intent(context, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }

            is SettingSideEffect.ShowToast -> {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    var usernameDialogVisible by remember {
        mutableStateOf(false)
    }

    SettingScreen(
        username = state.username,
        profileImageUrl = state.profileImageUrl,
        onImageChangeClick = {},
        onNameChangeClick = {
            usernameDialogVisible = true
        },
        onLogoutClick = viewModel::onLogoutClick
    )

    UsernameDialog(
        visible = usernameDialogVisible,
        initialUsername = state.username,
        onUsernameChange = viewModel::onUsernameChange,
        onDismissRequest = {
            usernameDialogVisible = false
        }
    )
}


@Composable
private fun SettingScreen(
    username: String,
    profileImageUrl: String?,
    onImageChangeClick: () -> Unit,
    onNameChangeClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            MGProfileImage(
                modifier = Modifier.size(180.dp), // java.reflect랑 혼동 x
                profileImageUrl = profileImageUrl,
            )
            IconButton(
                onClick = onImageChangeClick,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                // 아이콘 사이즈 조절하려고 Box로 감쌈
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(width = 1.dp, color = Color.White, shape = CircleShape)
                        .background(color = Color.White, shape = CircleShape)
                ) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(20.dp),
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        Text(
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable {
                    onNameChangeClick()
                },
            text = username,
            style = MaterialTheme.typography.headlineMedium
        )
        Button(modifier = Modifier.padding(top = 8.dp), onClick = onLogoutClick) {
            Text("로그아웃")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    MVIPracticeTheme {
        Surface {
            SettingScreen(
                "mandoo",
                "profileImageUrl",
                onImageChangeClick = {},
                onNameChangeClick = {},
                onLogoutClick = {}
            )
        }
    }
}