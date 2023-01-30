package com.example.mobile.ui.top_action_bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.datastore.DataStore
import com.example.local.model.Label
import com.example.mobile.cons.KEY_CLICK
import com.example.mobile.icons.BROOM_ICON
import com.example.mobile.icons.MENU_BURGER_ICON
import com.example.mobile.cons.SURFACE
import com.example.mobile.fp.getMaterialColor
import com.example.mobile.ui.AdaptingRow
import com.example.mobile.ui.settings_screen.makeSound
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteTopAppBar(
    searchNoteTitle: MutableState<String>,
    dataStore: DataStore?,
    scrollBehavior: TopAppBarScrollBehavior,
    drawerState: DrawerState,
    thisHomeScreen: Boolean,
    confirmationDialogState: MutableState<Boolean>?,
    expandedSortMenuState: MutableState<Boolean>?,
    searchScreen: String,
    label: MutableState<Label>?
) {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val thereIsSoundEffect = DataStore(ctx).thereIsSoundEffect.collectAsState(false)

    TopAppBar(
        navigationIcon = {
            Row {
                AnimatedVisibility(searchNoteTitle.value.isEmpty() && label?.value == Label()) {
                    AdaptingRow(
                        Modifier.width(40.dp),
                    ) {
                        Icon(
                            painterResource(MENU_BURGER_ICON),
                            null,
                            modifier = Modifier.clickable {
                                scope.launch {
                                    Unit.makeSound.invoke(ctx, KEY_CLICK, thereIsSoundEffect.value)
                                    drawerState.open()
                                }
                            }
                        )
                    }
                }
            }
        },
        title = {
            SearchField(
                title = searchNoteTitle,
                placeholder = searchScreen,
                label = label
            )
        },
        actions = {
            AnimatedVisibility(visible = searchNoteTitle.value.isEmpty() && label?.value == Label()) {
                AdaptingRow(
                    Modifier.width(90.dp)
                ) {
                    thisHomeScreen.let {
                        if (it) {
                            SortBy(
                                isShow = expandedSortMenuState,
                                dataStore = dataStore
                            )
                        } else {
                            Icon(
                                painterResource(BROOM_ICON),
                                null,
                                modifier = Modifier.clickable {
                                    confirmationDialogState?.value = true
                                })
                        }
                    }
                    Layout(
                        dataStore = dataStore
                    )
                }
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = getMaterialColor(SURFACE)
        ),
        scrollBehavior = scrollBehavior
    )
}