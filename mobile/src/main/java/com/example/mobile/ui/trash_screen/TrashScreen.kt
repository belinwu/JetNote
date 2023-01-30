package com.example.mobile.ui.trash_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.datastore.DataStore
import com.example.local.model.Entity
import com.example.local.model.Label
import com.example.local.model.Note
import com.example.mobile.cons.*
import com.example.mobile.ui.navigation_drawer.Screens.TRASH_SCREEN
import com.example.mobile.fp.filterBadWords
import com.example.mobile.fp.getMaterialColor
import com.example.mobile.ui.layouts.VerticalGrid
import com.example.mobile.ui.navigation_drawer.NavigationDrawer
import com.example.mobile.ui.note_card.NoteCard
import com.example.mobile.ui.top_action_bar.NoteTopAppBar
import com.example.mobile.ui.top_action_bar.dialogs.EraseDialog
import com.example.mobile.vm.EntityVM
import com.example.mobile.vm.NoteVM
import java.io.File

@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter",
    "UnusedMaterialScaffoldPaddingParameter"
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    viewModule: NoteVM = hiltViewModel(),
    entityVM: EntityVM = hiltViewModel(),
    navController: NavController,
) {
    val ctx = LocalContext.current
    val searchTitleState = remember { mutableStateOf("") }//.filterBadWords()
    val noteDataStore = DataStore(ctx)
    val currentLayout = noteDataStore.getLayout.collectAsState(true)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scaffoldState = rememberScaffoldState()
    val observerTrashedNotes: State<List<Entity>> =
        remember(entityVM, entityVM::allTrashedNotes).collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val confirmationDialogState = remember { mutableStateOf(false) }

    //
    if (confirmationDialogState.value) {
        EraseDialog(dialogState = confirmationDialogState) {
            viewModule.eraseTrash()
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            NavigationDrawer(
                drawerState = drawerState,
                navController = navController,
                searchLabel = null,
                searchTitle = searchTitleState
            )
        },
        drawerState = drawerState
    ) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .navigationBarsPadding(),
            scaffoldState = scaffoldState,
            backgroundColor = getMaterialColor(SURFACE),
            topBar = {
                NoteTopAppBar(
                    searchNoteTitle = searchTitleState,
                    dataStore = noteDataStore,
                    scrollBehavior = scrollBehavior,
                    drawerState = drawerState,
                    thisHomeScreen = false,
                    confirmationDialogState = confirmationDialogState,
                    expandedSortMenuState = null,
                    searchScreen = SEARCH_IN_TRASH,
                    label = remember { mutableStateOf(Label()) }
                )
            }
        ) {
            if (currentLayout.value) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        observerTrashedNotes.value.filter { entity ->
                            entity.note.title?.contains(searchTitleState.value, true) ?: true
                        }
                    ) { entity ->
                        NoteCard(
                            entity = entity,
                            navController = navController,
                            forScreen = TRASH_SCREEN,
                            selectionState = null,
                            selectedNotes = null
                        ) {
                            viewModule.deleteNote(Note(uid = entity.note.uid))
                            entity.note.uid.let {
                                File(ctx.filesDir.path + "/" + IMAGE_FILE, "$it.$JPEG").delete()
                                File(ctx.filesDir.path + "/" + AUDIO_FILE, "$it.$MP3").delete()
                            }
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        VerticalGrid(maxColumnWidth = 220.dp) {
                            observerTrashedNotes.value.filter { entity ->
                                entity.note.title?.contains(searchTitleState.value, true) ?: true
                            }.forEach { entity ->
                                NoteCard(
                                    entity = entity,
                                    navController = navController,
                                    forScreen = TRASH_SCREEN,
                                    selectionState = null,
                                    selectedNotes = null
                                ){
                                    viewModule.deleteNote(Note(uid = it.note.uid))
                                    it.note.uid.let {
                                        File(ctx.filesDir.path + "/" + IMAGE_FILE, "$it.$JPEG").delete()
                                        File(ctx.filesDir.path + "/" + AUDIO_FILE, "$it.$MP3").delete()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}