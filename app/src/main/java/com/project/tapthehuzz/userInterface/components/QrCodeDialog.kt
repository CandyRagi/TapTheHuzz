package com.project.tapthehuzz.userInterface.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.tapthehuzz.utils.QrCodeUtils

@Composable
fun QrCodeDialog(
    username: String,
    phoneNumber: String,
    onDismiss: () -> Unit,
    onGoToSettings: () -> Unit
) {
    val isLinked = phoneNumber.isNotBlank()

    val qrBitmap = remember(username, phoneNumber) {
        val content = if (isLinked) {
            QrCodeUtils.buildContactVCard(username, phoneNumber)
        } else {
            QrCodeUtils.buildContactVCard(username, "0000000000")
        }
        QrCodeUtils.generateQrCodeBitmap(content)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isLinked) "Scan to save contact" else "Phone number not linked",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Contact QR code",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .then(if (isLinked) Modifier else Modifier.blur(10.dp)),
                        alpha = if (isLinked) 1f else 0.35f
                    )

                    if (!isLinked) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.45f))
                                .clickable {
                                    onDismiss()
                                    onGoToSettings()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Link phone number in Settings",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                if (!isLinked) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            onDismiss()
                            onGoToSettings()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Go to Settings")
                    }
                }
            }
        }
    }
}
