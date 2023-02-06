package com.example.grqph.about_screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common_ui.Cons.APP_NAME
import com.example.common_ui.Cons.APP_VERSION
import com.example.common_ui.MatColors
import com.example.common_ui.MatColors.Companion.ON_SURFACE

@Composable
fun AboutContent() {
    val getMatColor = MatColors().getMaterialColor
    Text(
        text = about,
        fontSize = 20.sp,
        color = getMatColor(ON_SURFACE),
        modifier = Modifier.padding(20.dp)
    )
}

val about = "$APP_NAME a note-taking and todo management open-source application.\n\n" +
        " It is developed by City-Zouitel organization on github.\n\n" +
        " And It is intended for archiving and creating notes in which photos," +
        " audio and saved web links, numbers and map locations.\n\n" +
        "Version: $APP_VERSION"