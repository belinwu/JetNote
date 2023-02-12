package com.example.grqph.top_action_bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common_ui.AdaptingRow
import com.example.common_ui.Cons.KEY_CLICK
import com.example.common_ui.Icons.COPY_ICON
import com.example.common_ui.Icons.CROSS_ICON
import com.example.common_ui.Icons.SHARE_ICON
import com.example.common_ui.Icons.TRASH_ICON
import com.example.common_ui.SoundEffect
import com.example.common_ui.sharNote
import com.example.datastore.DataStore
import com.example.local.model.Note
import com.example.local.model.NoteAndLabel
import com.example.local.model.NoteAndTodo
import com.example.local.model.Todo
import com.example.graph.copyNote
import java.util.*
import kotlin.random.Random.Default.nextLong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopAppBar(
    noteVM: com.example.note.NoteVM = hiltViewModel(),
    noteAndLabelVM: com.example.tags.NoteAndLabelVM = hiltViewModel(),
    labelVM: com.example.tags.LabelVM = hiltViewModel(),
    todoVM: com.example.tasks.TodoVM = hiltViewModel(),
    noteAndTodoVM: com.example.tasks.NoteAndTodoVM = hiltViewModel(),
    selectionState: MutableState<Boolean>?,
    selectedNotes: SnapshotStateList<Note>?,
    undo: (Note) -> Unit
) {
    val ctx = LocalContext.current
    val thereIsSoundEffect = DataStore(ctx).thereIsSoundEffect.collectAsState(false).value

    val observeNotesAndLabels =
        remember(noteAndLabelVM, noteAndLabelVM::getAllNotesAndLabels).collectAsState()
    val observeLabels = remember(labelVM, labelVM::getAllLabels).collectAsState()

    val observeTodoList = remember(todoVM, todoVM::getAllTodoList).collectAsState()
    val observeNoteAndTodo =
        remember(noteAndTodoVM, noteAndTodoVM::getAllNotesAndTodo).collectAsState()

    val newUid = UUID.randomUUID()
    val sound = SoundEffect()

    TopAppBar(
        navigationIcon = { /**/ },
        title = {
            Row {
                // delete
                Icon(painter = painterResource(id = TRASH_ICON), contentDescription = null,
                    modifier = Modifier
                        .padding(7.dp)
                        .clickable {
                            sound.makeSound.invoke(ctx, KEY_CLICK, thereIsSoundEffect)
                            selectedNotes?.forEach {
                                noteVM.updateNote(
                                    Note(
                                        title = it.title,
                                        description = it.description,
                                        priority = it.priority,
                                        uid = it.uid,
                                        color = it.color,
                                        textColor = it.textColor,
                                        trashed = 1
                                    )
                                )
                                undo.invoke(it)
                            }
                            selectedNotes?.clear()
                            selectionState?.value = false
                        })

                // share
                AnimatedVisibility(visible = selectedNotes?.count() == 1) {
                    Row {
                        Icon(painter = painterResource(id = SHARE_ICON), contentDescription = null,
                            modifier = Modifier
                                .padding(7.dp)
                                .clickable {
                                    sound.makeSound.invoke(ctx, KEY_CLICK, thereIsSoundEffect)
                                    sharNote(
                                        ctx,
                                        selectedNotes?.single()?.title!!,
                                        selectedNotes.single().description!!
                                    ) {
                                        selectedNotes.clear()
                                        selectionState?.value = false
                                    }
                            })
                        // copy

                        Icon(painter = painterResource(id = COPY_ICON), contentDescription = null,
                            modifier = Modifier
                                .padding(7.dp)
                                .clickable {
                                    sound.makeSound.invoke(ctx, KEY_CLICK, thereIsSoundEffect)

                                    copyNote(ctx, noteVM, selectedNotes?.single()!!, newUid) {
                                    // copy each label.
                                    observeLabels.value.filter {
                                        observeNotesAndLabels.value.contains(
                                            NoteAndLabel(selectedNotes.single().uid, it.id)
                                        )
                                    }.forEach {
                                        noteAndLabelVM.addNoteAndLabel(
                                            NoteAndLabel(
                                                noteUid = newUid.toString(),
                                                labelId = it.id
                                            )
                                        )
                                    }

                                    // copy each todo item.
                                    observeTodoList.value.filter {
                                        observeNoteAndTodo.value.contains(
                                            NoteAndTodo(selectedNotes.single().uid, it.id)
                                        )
                                    }.forEach { todo ->
                                        nextLong().let {
                                            todoVM.addTotoItem(Todo(it, todo.item, todo.isDone))
                                            noteAndTodoVM.addNoteAndTodoItem(
                                                NoteAndTodo(
                                                    newUid.toString(),
                                                    it
                                                )
                                            )
                                        }
                                    }
                                }
                                selectedNotes.clear()
                                selectionState?.value = false
                            }
                        )
                    }
                }
            }
        },
        actions = {
            AdaptingRow(
                modifier = Modifier.width(60.dp)
            ) {
                Icon(
                    painter = painterResource(id = CROSS_ICON),
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        selectionState?.value = false
                        selectedNotes?.clear()
                    }
                )
                // number of selected notes.
                Text(text = selectedNotes?.count().toString(), fontSize = 24.sp)
            }
        }
    )
}