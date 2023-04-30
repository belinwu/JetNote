package com.example.graph.top_action_bar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common_ui.AdaptingRow
import com.example.common_ui.Cons
import com.example.common_ui.DataStoreVM
import com.example.common_ui.Icons
import com.example.graph.sound
import com.example.graph.top_action_bar.selection_bars.SelectionCount
import com.example.local.model.Note
import com.example.note.NoteVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashSelectionTopAppBar(
    dataStoreVM: DataStoreVM = hiltViewModel(),
    noteVM: NoteVM = hiltViewModel(),
    trashSelectionState: MutableState<Boolean>?,
    selectedNotes: SnapshotStateList<Note>?,

    ) {
    val ctx = LocalContext.current

    val thereIsSoundEffect = remember(dataStoreVM, dataStoreVM::getSound).collectAsState()

    TopAppBar(
        navigationIcon = {},
        title = {
            // delete
            Icon(
                painter = painterResource(id = Icons.TRASH_ICON),
                contentDescription = null,
                modifier = Modifier
                    .padding(7.dp)
                    .clickable {
                        sound.makeSound.invoke(ctx, Cons.KEY_CLICK, thereIsSoundEffect.value)
                        selectedNotes?.forEach {
                            noteVM.deleteNote(it)
                        }
                        selectedNotes?.clear()
                        trashSelectionState?.value = false
                    }
            )
        },
        actions = {
            AdaptingRow(
                modifier = Modifier.width(60.dp)
            ) {
                SelectionCount(selectionState = trashSelectionState, selectedNotes = selectedNotes)
            }
        }
    )
}