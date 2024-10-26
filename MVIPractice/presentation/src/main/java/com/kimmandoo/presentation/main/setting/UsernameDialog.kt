package com.kimmandoo.presentation.main.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.kimmandoo.presentation.theme.MVIPracticeTheme

@Composable
fun UsernameDialog(
    visible: Boolean = false,
    initialUsername: String,
    onUsernameChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    // setting 스크린에서 다이얼로그로 입력을 받을 예정
    var username by remember { mutableStateOf(initialUsername) }
    if (visible) {
        Dialog(onDismissRequest = onDismissRequest) {
            Surface {
                Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                    // parameter로 받아온 username을 그대로 넣으면 TextField 수정시 내용이 안바뀜
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = username,
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        ),
                        onValueChange = {
                            username = it
                        })

                    Row {
                        TextButton(modifier = Modifier.weight(1f), onClick = {
                            onUsernameChange(username)
                            onDismissRequest()
                        }) {
                            Text("변경")
                        }
                        TextButton(modifier = Modifier.weight(1f), onClick = { onDismissRequest() }) {
                            Text("취소")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun UsernameDialogPreview() {
    MVIPracticeTheme {
        UsernameDialog(
            visible = true,
            initialUsername = "Graciela Patel",
            onUsernameChange = {},
            onDismissRequest = {})
    }
}