package city.zouitel.note.ui.edit_screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import city.zouitel.links.model.NoteAndLink
import city.zouitel.links.ui.CacheLinks
import city.zouitel.links.ui.LinkPart
import city.zouitel.links.ui.LinkVM
import city.zouitel.links.ui.NoteAndLinkVM
import city.zouitel.audios.MediaPlayerViewModel
import city.zouitel.note.ui.bottom_bar.AddEditBottomBar
import city.zouitel.note.DataViewModel
import city.zouitel.note.model.Data
import city.zouitel.recoder.ui.RecordingNote
import city.zouitel.reminder.ui.RemindingNote
import city.zouitel.systemDesign.Cons.AUDIOS
import city.zouitel.systemDesign.Cons.HOME_ROUTE
import city.zouitel.systemDesign.Cons.IMAGES
import city.zouitel.systemDesign.Cons.JPEG
import city.zouitel.systemDesign.Cons.KEY_STANDARD
import city.zouitel.systemDesign.Cons.MP3
import city.zouitel.systemDesign.Cons.NUL
import city.zouitel.systemDesign.DataStoreVM
import city.zouitel.systemDesign.Icons
import city.zouitel.systemDesign.Icons.CIRCLE_ICON_18
import city.zouitel.systemDesign.Icons.EDIT_ICON
import city.zouitel.systemDesign.ImageDisplayed
import city.zouitel.systemDesign.MaterialColors
import city.zouitel.systemDesign.MaterialColors.Companion.OUT_LINE_VARIANT
import city.zouitel.systemDesign.SoundEffect
import city.zouitel.systemDesign.decodeUrl
import city.zouitel.systemDesign.findUrlLink
import city.zouitel.tags.viewmodel.NoteAndTagViewModel
import city.zouitel.tags.viewmodel.TagViewModel
import city.zouitel.tags.model.NoteAndTag
import city.zouitel.tasks.viewmodel.NoteAndTaskViewModel
import city.zouitel.tasks.viewmodel.TaskViewModel
import city.zouitel.tasks.model.NoteAndTask
import city.zouitel.tasks.model.Task
import com.google.accompanist.flowlayout.FlowRow
import org.koin.androidx.compose.koinViewModel
import java.io.File

@SuppressLint(
    "UnrememberedMutableState",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun NoteEdit(
    navController: NavController,
    dataViewModel: DataViewModel = koinViewModel(),
    exoViewModule: MediaPlayerViewModel = koinViewModel(),
    noteAndLabelVM: NoteAndTagViewModel = koinViewModel(),
    tagViewModel: TagViewModel = koinViewModel(),
    taskViewModel: TaskViewModel = koinViewModel(),
    noteAndTodoVM: NoteAndTaskViewModel = koinViewModel(),
    dataStoreVM: DataStoreVM = koinViewModel(),
    linkVM: LinkVM = koinViewModel(),
    noteAndLinkVM: NoteAndLinkVM = koinViewModel(),
    uid:String,
    title:String?,
    description:String?,
    color: Int,
    textColor: Int,
    priority: String,
    audioDuration:Int,
    reminding: Long
) {

    val ctx = LocalContext.current
    val internalPath = ctx.filesDir.path
    val keyboardManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = FocusRequester()

    val thereIsSoundEffect = remember(dataStoreVM, dataStoreVM::getSound).collectAsState()

    val isTitleFieldFocused = remember { mutableStateOf(false) }
    val isDescriptionFieldFocused = remember { mutableStateOf(false) }

    val observeNotesAndLabels = remember(noteAndLabelVM,noteAndLabelVM::getAllNotesAndTags).collectAsState()
    val observeLabels = remember(tagViewModel, tagViewModel::getAllLTags).collectAsState()

    val observerLinks = remember(linkVM, linkVM::getAllLinks).collectAsState()
    val observerNoteAndLink =
        remember(noteAndLinkVM, noteAndLinkVM::getAllNotesAndLinks).collectAsState()

    val observeTodoList = remember(taskViewModel, taskViewModel::getAllTaskList).collectAsState()
    val observeNoteAndTodo =
        remember(noteAndTodoVM, noteAndTodoVM::getAllNotesAndTask).collectAsState()

    val getMatColor = MaterialColors().getMaterialColor
    val sound = SoundEffect()

    val titleState = rememberSaveable {
        mutableStateOf(
            if(title == NUL || title.isNullOrEmpty()) null else decodeUrl.invoke(title)
        )
    }

    val descriptionState = rememberSaveable {
        mutableStateOf(
            if(description == NUL) null else decodeUrl.invoke(description)
        )
    }

    val backgroundColorState = rememberSaveable { mutableStateOf(color) }
    val textColorState = rememberSaveable { mutableStateOf(textColor) }

    val priorityState = remember { mutableStateOf(priority) }

    val mediaFile = "$internalPath/$AUDIOS/$uid.$MP3"

    //
    val dateState = mutableStateOf(Calendar.getInstance().time)

    val remindingDialogState = remember { mutableStateOf(false) }

    val recordDialogState = remember { mutableStateOf(false) }

    val imagePath = "$internalPath/$IMAGES/$uid.$JPEG"
    val bitImg = BitmapFactory.decodeFile(imagePath)

    val photoState = remember { mutableStateOf<Bitmap?>(bitImg) }
    val imageUriState = remember { mutableStateOf<Uri?>(File(imagePath).toUri()) }
    val img by rememberSaveable { mutableStateOf(photoState) }

    val chooseImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            imageUriState.value = it
            dataViewModel::decodeBitmapImage.invoke(img, photoState, it!!, ctx)
            img.value = photoState.value
            dataViewModel::saveImageLocally.invoke(
                img.value, "$internalPath/$IMAGES", "$uid.$JPEG"
            )
        }

    val sheetState = rememberBottomSheetScaffoldState()

    val remindingValue = remember { mutableStateOf(reminding) }

    val audioDurationState = remember { mutableStateOf(0) }
    val gifUri = remember { mutableStateOf<Uri?>(null) }

    Scaffold(
        modifier = Modifier
            .navigationBarsPadding(),
        floatingActionButton = {
//            Column {
//                AnimatedVisibility(visible = sheetState.bottomSheetState.isCollapsed) {
                    FloatingActionButton(
                        containerColor = getMatColor(OUT_LINE_VARIANT),
                        contentColor = contentColorFor(backgroundColor = getMatColor(OUT_LINE_VARIANT)),
                        onClick = {
                            sound.makeSound.invoke(ctx, KEY_STANDARD,thereIsSoundEffect.value)

                            dataViewModel.editData(
                                Data(
                                    title = if(titleState.value.isNullOrBlank()) null else titleState.value ,
                                    description = if(descriptionState.value.isNullOrBlank()) null else descriptionState.value,
                                    priority = priorityState.value,
                                    uid = uid,
                                    audioDuration = audioDuration,
                                    reminding = remindingValue.value,
                                    date = dateState.value.toString(),
                                    trashed = 0,
                                    color = backgroundColorState.value,
                                    textColor = textColorState.value,
                                )
                            )
                            navController.navigate(HOME_ROUTE)
                        }) {
                        Icon(
                            painter = painterResource(id = EDIT_ICON),
                            null
                        )
                    }
//                }
//            }
        },
        bottomBar = {
            AddEditBottomBar(
                navController = navController,
                recordDialogState = recordDialogState,
                remindingDialogState = remindingDialogState,
                note = Data(uid = uid),
                backgroundColorState = backgroundColorState,
                textColorState = textColorState,
                priorityColorState = priorityState,
                notePriority = priorityState,
                imageLaunch = chooseImageLauncher,
                titleFieldState = titleState,
                descriptionFieldState = descriptionState,
                isTitleFieldSelected = isTitleFieldFocused,
                isDescriptionFieldSelected = isDescriptionFieldFocused,
                isCollapsed = null
            )
        }
    ) {
        // recording dialog visibility.
        if (recordDialogState.value) {
            RecordingNote(uid = uid, dialogState = recordDialogState)
        }

        // reminding dialog visibility.
        if (remindingDialogState.value) {
            RemindingNote(
                dialogState = remindingDialogState,
                remindingValue = remindingValue,
                title = titleState.value,
                message = descriptionState.value,
                uid = uid
            )
        }

        LazyColumn(
            Modifier
                .background(Color(backgroundColorState.value))
                .fillMaxSize()
        ) {

            // display the image.
            item {
                ImageDisplayed(media = img.value?.asImageBitmap())
            }

            // display the media player.
            item {
                Spacer(modifier = Modifier.height(18.dp))
                if (
                    File(mediaFile).exists() && !recordDialogState.value
                ) {
                    city.zouitel.audios.NoteMediaPlayer(localMediaUid = uid)
                    audioDurationState.value =
                        exoViewModule.getMediaDuration(ctx, mediaFile).toInt()

                }
            }

            // The Title.
            item {

                OutlinedTextField(
                    value = titleState.value ?: "",
                    onValueChange = { titleState.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .focusRequester(focusRequester)
                        .onFocusEvent {
                            isTitleFieldFocused.value = it.isFocused
                        },
                    placeholder = {
                        Text("Title", color = Color.Gray, fontSize = 24.sp)
                    },
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Default,
                        color = Color(textColorState.value)
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            keyboardManager.moveFocus(FocusDirection.Next)
                        }
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = contentColorFor(backgroundColor = Color(backgroundColorState.value))
                    )
                )
            }

            //The Description.
            item {

                OutlinedTextField(
                    value = descriptionState.value ?: "",
                    onValueChange = {
                        descriptionState.value = it

                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Note", color = Color.Gray, fontSize = 19.sp)
                    },
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Default,
                        color = Color(textColorState.value)
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Default
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            keyboardManager.clearFocus()
                        }
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // Link display.
            item {
                findUrlLink(descriptionState.value)?.let { url ->
                    CacheLinks(
                        linkVM = linkVM,
                        noteAndLinkVM = noteAndLinkVM,
                        noteUid = uid,
                        url = url
                    )
                }
                // for refresh this screen.
                observerLinks.value.filter {
                    observerNoteAndLink.value.contains(
                        NoteAndLink(uid, it.id)
                    )
                }.forEach { _link ->
                    LinkPart(
                        linkVM = linkVM,
                        noteAndLinkVM = noteAndLinkVM,
                        noteUid = uid,
                        swipeable = true,
                        link = _link
                    )
                }
            }

            // display all added tagEntities.
            item {
                FlowRow {
                    observeLabels.value.filter {
                        observeNotesAndLabels.value.contains(
                            NoteAndTag(uid, it.id)
                        )
                    }.forEach {
                        AssistChip(
                            onClick = { },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = CIRCLE_ICON_18),
                                    contentDescription = null,
                                    tint = Color(it.color),
                                    modifier = Modifier.size(10.dp)
                                )
                            },
                            label = {
                                it.label?.let { it1 -> Text(it1) }
                            }
                        )
                    }
                }
            }

            // display the todo list.
            item {
                observeTodoList.value.filter {
                    observeNoteAndTodo.value.contains(
                        NoteAndTask(uid, it.id)
                    )
                }.forEach { todo ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = todo.isDone,
                            onCheckedChange = {
                                taskViewModel.updateTotoItem(
                                    Task(
                                        id = todo.id,
                                        item = todo.item,
                                        isDone = !todo.isDone
                                    )
                                )
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.Gray,
                                uncheckedColor = Color(textColorState.value)
                            )
                        )

                        todo.item?.let { item ->
                            Text(
                                text = item,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    textDecoration = if (todo.isDone) TextDecoration.LineThrough else TextDecoration.None,
                                    color = if (todo.isDone) Color.Gray else Color(textColorState.value)
                                )
                            )
                        }
                    }
                }
            }

//            // void space.
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }
        }
    }

//    BottomSheetScaffold(
//        scaffoldState = sheetState,
//        sheetPeekHeight = 50.dp,
//        backgroundColor = Color(backgroundColorState.value),
//        modifier = Modifier
//            .navigationBarsPadding()
//            .imePadding(),
//        floatingActionButton = {
//            Column {
//                AnimatedVisibility(visible = sheetState.bottomSheetState.isCollapsed) {
//                    FloatingActionButton(
//                        containerColor = getMatColor(OUT_LINE_VARIANT),
//                        contentColor = contentColorFor(backgroundColor = getMatColor(OUT_LINE_VARIANT)),
//                        onClick = {
//                            sound.makeSound.invoke(ctx, KEY_STANDARD,thereIsSoundEffect.value)
//
//                            dataViewModel.editData(
//                                Data(
//                                    title = if(titleState.value.isNullOrBlank()) null else titleState.value ,
//                                    description = if(descriptionState.value.isNullOrBlank()) null else descriptionState.value,
//                                    priority = priorityState.value,
//                                    uid = uid,
//                                    audioDuration = audioDuration,
//                                    reminding = remindingValue.value,
//                                    date = dateState.value.toString(),
//                                    trashed = 0,
//                                    color = backgroundColorState.value,
//                                    textColor = textColorState.value,
//                                )
//                            )
//                            navController.navigate(HOME_ROUTE)
//                        }) {
//                        Icon(
//                            painter = painterResource(id = EDIT_ICON),
//                            null
//                        )
//                    }
//                }
//            }
//        },
//        sheetContent = {
//            AddEditBottomBar(
//                navController = navController,
//                recordDialogState = recordDialogState,
//                remindingDialogState = remindingDialogState,
//                note = Data(uid = uid),
//                backgroundColorState = backgroundColorState,
//                textColorState = textColorState,
//                priorityColorState = priorityState,
//                notePriority = priorityState,
//                imageLaunch = chooseImageLauncher,
//                titleFieldState = titleState,
//                descriptionFieldState = descriptionState,
//                isTitleFieldSelected = isTitleFieldFocused,
//                isDescriptionFieldSelected = isDescriptionFieldFocused,
//                isCollapsed = null
//            )
//        }) {
//
//        // recording dialog visibility.
//        if (recordDialogState.value) {
//            RecordingNote(uid = uid, dialogState = recordDialogState)
//        }
//
//        // reminding dialog visibility.
//        if (remindingDialogState.value) {
//            RemindingNote(
//                dialogState = remindingDialogState,
//                remindingValue = remindingValue,
//                title = titleState.value,
//                message = descriptionState.value,
//                uid = uid
//            )
//        }
//
//        LazyColumn(Modifier.fillMaxSize()) {
//            // display the image.
//            item {
//                ImageDisplayed(media = img.value?.asImageBitmap())
//            }
//            // display the media player.
//            item {
//                Spacer(modifier = Modifier.height(18.dp))
//                if (
//                    File(mediaFile).exists() && !recordDialogState.value
//                ) {
//                    city.zouitel.audios.NoteMediaPlayer(localMediaUid = uid)
//                    audioDurationState.value =
//                        exoViewModule.getMediaDuration(ctx,mediaFile).toInt()
//                }
//            }
//
//            // The Title.
//            item {
//                OutlinedTextField(
//                    value = titleState.value ?: "",
//                    onValueChange = { titleState.value = it },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 20.dp)
//                        .focusRequester(focusRequester)
//                        .onFocusEvent {
//                            isTitleFieldFocused.value = it.isFocused
//                        },
//                    placeholder = {
//                        Text("Title", color = Color.Gray, fontSize = 24.sp)
//                    },
//                    textStyle = TextStyle(
//                        fontSize = 24.sp,
//                        fontWeight = FontWeight.Normal,
//                        fontFamily = FontFamily.Default,
//                        color = Color(textColorState.value)
//                    ),
//                    keyboardOptions = KeyboardOptions(
//                        capitalization = KeyboardCapitalization.Sentences,
//                        autoCorrect = false,
//                        keyboardType = KeyboardType.Text,
//                        imeAction = ImeAction.Next
//                    ),
//                    keyboardActions = KeyboardActions(
//                        onNext = {
//                            keyboardManager.moveFocus(FocusDirection.Next)
//                        }
//                    ),
//                    colors = TextFieldDefaults.outlinedTextFieldColors(
//                        focusedBorderColor = Color.Transparent,
//                        unfocusedBorderColor = Color.Transparent,
//                        focusedTextColor = contentColorFor(backgroundColor = Color(backgroundColorState.value))
//                    )
//                )
//            }
//
//            //The Description.
//            item {
//                OutlinedTextField(
//                    value = descriptionState.value ?: "",
//                    onValueChange = { descriptionState.value = it },
//                    modifier = Modifier.fillMaxWidth(),
//                    placeholder = {
//                        Text("Note", color = Color.Gray, fontSize = 19.sp)
//                    },
//                    textStyle = TextStyle(
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Normal,
//                        fontFamily = FontFamily.Default,
//                        color = Color(textColorState.value)
//                    ),
//                    keyboardOptions = KeyboardOptions(
//                        capitalization = KeyboardCapitalization.Sentences,
//                        autoCorrect = false,
//                        keyboardType = KeyboardType.Text,
//                        imeAction = ImeAction.Default
//                    ),
//                    keyboardActions = KeyboardActions(
//                        onDone = {
//                            keyboardController?.hide()
//                            keyboardManager.clearFocus()
//                        }
//                    ),
//                    colors = TextFieldDefaults.outlinedTextFieldColors(
//                        focusedBorderColor = Color.Transparent,
//                        unfocusedBorderColor = Color.Transparent
//                    )
//                )
//            }
//
//            // Link display.
//            item {
//                findUrlLink(descriptionState.value)?.let { url ->
//                    CacheLinks(
//                        linkVM = linkVM,
//                        noteAndLinkVM = noteAndLinkVM,
//                        noteUid = uid,
//                        url = url
//                    )
//
//                }
//                // for refresh this screen.
//                observerLinks.value.filter {
//                    observerNoteAndLink.value.contains(
//                        NoteAndLink(uid, it.id)
//                    )
//                }.forEach { _link ->
//                    LinkPart(
//                        linkVM = linkVM,
//                        noteAndLinkVM = noteAndLinkVM,
//                        noteUid = uid,
//                        swipeable = true,
//                        link = _link
//                    )
//                }
//            }
//
//            // display all added tagEntities.
//            item {
//                FlowRow {
//                    observeLabels.value.filter {
//                        observeNotesAndLabels.value.contains(
//                            NoteAndTag(uid, it.id)
//                        )
//                    }.forEach {
//                        AssistChip(
//                            onClick = { },
//                            leadingIcon = {
//                                Icon(
//                                    painter = painterResource(id = CIRCLE_ICON_18),
//                                    contentDescription = null,
//                                    tint = Color(it.color),
//                                    modifier = Modifier.size(10.dp)
//                                )
//                            },
//                            label = {
//                                it.label?.let { it1 -> Text(it1) }
//                            }
//                        )
//                    }
//                }
//            }
//
//            // display the todo list.
//            item {
//                observeTodoList.value.filter {
//                    observeNoteAndTodo.value.contains(
//                        NoteAndTask(uid, it.id)
//                    )
//                }.forEach { todo ->
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Checkbox(
//                            checked = todo.isDone,
//                            onCheckedChange = {
//                                taskViewModel.updateTotoItem(
//                                    Task(
//                                        id = todo.id,
//                                        item = todo.item,
//                                        isDone = !todo.isDone
//                                    )
//                                )
//                            },
//                            colors = CheckboxDefaults.colors(
//                                checkedColor = Color.Gray,
//                                uncheckedColor = Color(textColorState.value)
//                            )
//                        )
//
//                        todo.item?.let { item ->
//                            Text(
//                                text = item,
//                                fontSize = 14.sp,
//                                style = TextStyle(
//                                    textDecoration = if (todo.isDone) TextDecoration.LineThrough else TextDecoration.None,
//                                    color = if (todo.isDone) Color.Gray else Color(textColorState.value)
//                                )
//                            )
//                        }
//                    }
//                }
//            }
//
////            // void space.
//            item {
//                Box(modifier = Modifier
//                    .fillMaxWidth()
//                    .height(300.dp))
//            }
//        }
//    }
}
