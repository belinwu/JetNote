package com.example.mobile.ui.labels_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.local.model.Label
import com.example.local.model.NoteAndLabel
import com.example.mobile.cons.NULL
import com.example.mobile.cons.SURFACE
import com.example.mobile.cons.SURFACE_TINT
import com.example.mobile.fp.filterBadWords
import com.example.mobile.fp.getMaterialColor
import com.example.mobile.icons.CIRCLE_ICON_18
import com.example.mobile.icons.FULL_LABEL_ICON
import com.example.mobile.icons.OUTLINE_LABEL_ICON
import com.example.mobile.ui.layouts.HashTagLayout
import com.example.mobile.vm.LabelVM
import com.example.mobile.vm.NoteAndLabelVM
import com.google.accompanist.flowlayout.FlowRow

@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter",
    "StateFlowValueCalledInComposition",
    "UnrememberedMutableState",
    "UnusedMaterialScaffoldPaddingParameter"
)
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun Labels(
    labelVM: LabelVM = hiltViewModel(),
    noteAndLabelVM: NoteAndLabelVM = hiltViewModel(),
    noteUid: String?,
) {

    val observeLabels = remember(labelVM, labelVM::getAllLabels).collectAsState()
    val observeNotesAndLabels =
        remember(noteAndLabelVM, noteAndLabelVM::getAllNotesAndLabels).collectAsState()

    val idState = remember { mutableStateOf(-1L) }
    val labelState = remember { mutableStateOf("") }//.filterBadWords()
    val colorState = remember { mutableStateOf(Color.Transparent.toArgb()) }

    val labelDialogState = remember { mutableStateOf(false) }

    if (labelDialogState.value) {
        LabelDialogColors(
            dialogState = labelDialogState,
            idState = idState,
            labelState = labelState,
            colorState = colorState
        )
    }
    Scaffold(
        modifier = Modifier
            .navigationBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(getMaterialColor(SURFACE))
                .padding(top = 25.dp)
        ) {

            item {
                if (noteUid == NULL) {
                    HashTagLayout(
                        labelDialogState = labelDialogState,
                        hashTags = observeLabels.value,
                        idState = idState,
                        labelState = labelState
                    )
                } else {
                    FlowRow(
                        mainAxisSpacing = 3.dp
                    ) {
                        observeLabels.value.forEach { label ->
                            ElevatedFilterChip(
                                selected = true,
                                onClick = {
                                    if (observeNotesAndLabels.value.contains(
                                            NoteAndLabel(noteUid!!, label.id)
                                        )
                                    ) {
                                        noteAndLabelVM.deleteNoteAndLabel(
                                            NoteAndLabel(
                                                noteUid = noteUid,
                                                labelId = label.id
                                            )
                                        )
                                    } else {
                                        noteAndLabelVM.addNoteAndLabel(
                                            NoteAndLabel(
                                                noteUid = noteUid,
                                                labelId = label.id
                                            )
                                        )
                                    }
                                },
                                leadingIcon = {
                                    if (
                                        observeNotesAndLabels.value.contains(
                                            NoteAndLabel(noteUid!!, label.id)
                                        )
                                    ) {
                                        Icon(
                                            painterResource(FULL_LABEL_ICON), null,
                                            tint = if (label.color == Color.Transparent.toArgb()) {
                                                getMaterialColor(SURFACE_TINT)
                                            } else Color(label.color)
                                        )
                                    } else {
                                        Icon(
                                            painterResource(OUTLINE_LABEL_ICON), null,
                                            tint = if (label.color == Color.Transparent.toArgb()) {
                                                getMaterialColor(SURFACE_TINT)
                                            } else Color(label.color)
                                        )
                                    }
                                },
                                label = {
                                    label.label?.let { Text(it) }
                                }
                            )
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = labelState.value,
                    onValueChange = { labelState.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(getMaterialColor(SURFACE)),
                    placeholder = {
                        Text("Label..", color = Color.Gray, fontSize = 19.sp)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = CIRCLE_ICON_18),
                            contentDescription = null,
                            tint = Color(colorState.value)
                        )
                    },
                    maxLines = 1,
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Default,
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (observeLabels.value.any { it.id == idState.value }) {
                                labelVM.updateLabel(
                                    Label(
                                        id = idState.value,
                                        label = labelState.value,
                                        color = colorState.value
                                    )
                                )
                            } else {
                                labelVM.addLabel(
                                    Label(
                                        label = labelState.value,
                                        color = colorState.value
                                    )
                                )
                            }.invokeOnCompletion {
                                labelState.value = ""
                                idState.value = -1
                                colorState.value = Color.Transparent.toArgb()
                            }
                        }
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }
    }
}



