package com.example.mobile.ui.add_and_edit.bottom_bar

import android.Manifest.permission
import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.example.datastore.DataStore
import com.example.local.model.Note
import com.example.mobile.cons.*
import com.example.mobile.fp.getColorOfPriority
import com.example.mobile.fp.getPriorityOfColor
import com.example.mobile.icons.*
import com.example.mobile.ui.coloration.listOfPriorityColors
import com.example.mobile.ui.settings_screen.makeSound
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
internal fun Plus(
    isShow: MutableState<Boolean>,
    note: Note,
    navController: NavController,
    imageLaunch: ManagedActivityResultLauncher<String, Uri?>,
    recordDialogState: MutableState<Boolean>,
    priorityColorState: MutableState<String>
) {
    val ctx = LocalContext.current
    val thereIsSoundEffect = DataStore(ctx).thereIsSoundEffect.collectAsState(false)

    val permissionState = rememberMultiplePermissionsState(
        listOf(
            permission.RECORD_AUDIO,
            permission.WRITE_EXTERNAL_STORAGE,
        )
    ) {
        recordDialogState.value = it.values.all { true }
    }
    val currentColor = remember { mutableStateOf(getColorOfPriority(priorityColorState.value)) }

    DropdownMenu(
        expanded = isShow.value,
        onDismissRequest = {
            isShow.value = false
        },
        properties = PopupProperties(
            focusable = true
        )
    ) {
        DropdownMenuItem(
            text = { Text(text = "Add Image", fontSize = 18.sp) },
            leadingIcon = { Icon(painterResource(IMAGE_ICON), null) },
            onClick = {
                imageLaunch.launch("image/*")
                    .makeSound(ctx, KEY_CLICK, thereIsSoundEffect.value)
                isShow.value = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = "Take Photo", fontSize = 18.sp) },
            leadingIcon = { Icon(painterResource(CAMERA_ICON), null) },
            onClick = {
//                navController.navigate(
//                    CAMERA_ROUTE + "/" +
//                            note.uid
//                )
//                isShow.value = false
                Toast.makeText(ctx, "Coming Soon.", Toast.LENGTH_SHORT).show()
            },
            enabled = false
        )
        DropdownMenuItem(
            text = { Text(text = "Record", fontSize = 18.sp) },
            leadingIcon = { Icon(painterResource(MIC_ICON), null) },
            onClick = {
                permissionState.launchMultiplePermissionRequest()
                    .makeSound(ctx, KEY_CLICK, thereIsSoundEffect.value)
                recordDialogState.value = permissionState.allPermissionsGranted
                isShow.value = false
            }
        )

        DropdownMenuItem(
            text = { Text(text = "Drawing", fontSize = 18.sp) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = GESTURE_ICON),
                    contentDescription = null
                )
            },
            onClick = {
                navController.navigate(
                    route = DRAW_ROUTE + "/" +
                            note.title + "/" +
                            note.description + "/" +
                            note.color + "/" +
                            note.textColor + "/" +
                            note.priority + "/" +
                            note.uid + "/" +
                            note.audioDuration + "/" +
                            note.reminding
                ).makeSound(ctx, KEY_CLICK, thereIsSoundEffect.value)
                isShow.value = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = "Labels", fontSize = 18.sp) },
            leadingIcon = { Icon(painterResource(TAGS_ICON), null) },
            onClick = {
                navController.navigate("labels/${note.uid}")
                    .makeSound(ctx, KEY_CLICK, thereIsSoundEffect.value)
                isShow.value = false
            }
        )
        DropdownMenuItem(
            text = { Text(text = "Todo List", fontSize = 18.sp) },
            leadingIcon = { Icon(painterResource(LIST_CHECK_ICON), null) },
            onClick = {
                navController.navigate("todo/${note.uid}")
                    .makeSound(ctx, KEY_CLICK, thereIsSoundEffect.value)
                isShow.value = false
            }
        )
        DropdownMenuItem(
            text = {
                Row {
                    listOfPriorityColors.forEach {
                        Spacer(modifier = Modifier.width(3.dp))
                        Canvas(
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    currentColor.value = it
                                    priorityColorState.value = getPriorityOfColor(it)
                                    Unit.makeSound.invoke(ctx, KEY_CLICK, thereIsSoundEffect.value)
                                }
                        ) {
                            drawArc(
                                color = if (it == Color.Transparent) Color.Gray else it,
                                startAngle = 1f,
                                sweepAngle = 360f,
                                useCenter = true,
                                style =
                                if (currentColor.value == it) {
                                    Stroke(width = 5f, cap = StrokeCap.Round)
                                } else {
                                    Fill
                                }
                            )
                        }
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }
            },
            onClick = {
                Unit.makeSound(ctx, KEY_STANDARD, thereIsSoundEffect.value)
            }
        )
    }
}

