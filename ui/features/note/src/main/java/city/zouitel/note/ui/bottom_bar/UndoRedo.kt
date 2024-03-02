package city.zouitel.note.ui.bottom_bar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import city.zouitel.systemDesign.Cons.KEY_CLICK
import city.zouitel.systemDesign.DataStoreVM
import city.zouitel.systemDesign.Icons.REDO_ICON
import city.zouitel.systemDesign.Icons.UNDO_ICON
import city.zouitel.systemDesign.MaterialColors
import city.zouitel.systemDesign.MaterialColors.Companion.SURFACE_VARIANT
import city.zouitel.systemDesign.SoundEffect
import org.koin.androidx.compose.koinViewModel

// TODO: need improvement.
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UndoRedo(
    dataStoreVM: DataStoreVM = koinViewModel(),
    titleFieldState: MutableState<String?>,
    descriptionFieldState: MutableState<String?>,
    isTitleFieldSelected : MutableState<Boolean>,
    isDescriptionFieldSelected : MutableState<Boolean>,
    textState: TextFieldState
) {
    val titleStack = remember { mutableStateListOf<String>() }
    val descriptionStack = remember { mutableStateListOf<String>() }

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val thereIsSoundEffect = remember(dataStoreVM, dataStoreVM::getSound).collectAsState()

    val getMatColor = MaterialColors().getMaterialColor
    val sound = SoundEffect()

    IconButton(
        modifier = Modifier
            .size(20.dp)
            .combinedClickable(
                onLongClick = {
                    // To make vibration.
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            ) {},
        onClick = {
            sound.makeSound(context, KEY_CLICK, thereIsSoundEffect.value)
            textState.undoState.undo()
        },
        enabled = textState.undoState.canUndo
    ) {
        Icon(
            painter = painterResource(id = UNDO_ICON),
            contentDescription = null,
            tint = contentColorFor(backgroundColor = getMatColor(SURFACE_VARIANT)),
        )
    }

    IconButton(
        modifier = Modifier
            .size(20.dp)
            .combinedClickable(
                onLongClick = {
                    // To make vibration.
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            ) {},
        onClick = {
            sound.makeSound(context, KEY_CLICK, thereIsSoundEffect.value)
            textState.undoState.redo()
        },
        enabled = textState.undoState.canRedo
    ) {
        Icon(
            painter = painterResource(id = REDO_ICON),
            contentDescription = null,
            tint = contentColorFor(backgroundColor = getMatColor(SURFACE_VARIANT)),
        )
    }
}
