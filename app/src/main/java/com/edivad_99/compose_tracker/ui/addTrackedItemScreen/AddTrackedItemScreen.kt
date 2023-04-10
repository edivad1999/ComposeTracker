package com.edivad_99.compose_tracker.ui.addTrackedItemScreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Space
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.DialogHost
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import domain.Category
import domain.DataResponse
import domain.TrackedItem
import org.koin.androidx.compose.get
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf

@Composable
fun AddTrackedItemScreen(
    availableCategories: List<Category>,
    model: TrackedItemModel = get(parameters = { parametersOf(availableCategories) }),
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(modifier = Modifier.padding(48.dp)) {
            Column(modifier = Modifier.padding(8.dp)) {
                if (model.sendState != null) {
                    SendStateScreen(state = model.sendState)
                } else {
                    val state by model.state.collectAsState()
                    InsertData(
                        state = state,
                        selectableCategories = model.categories,
                        onNameChange = model::onNameChange,
                        onCoverChange = model::onCoverChange,
                        onCategoryChange = model::onCategoryChange
                    )
                }
            }
        }
    }
}

@Composable
fun SendStateScreen(state: DataResponse<TrackedItem>) {
    Text(text = state.toString())

}

@Composable
fun InsertData(
    state: TrackedItemState,
    selectableCategories: List<Category>,
    onNameChange: (String) -> Unit,
    onCoverChange: (Uri?) -> Unit,
    onCategoryChange: (Category) -> Unit
) {
    var dropDownExpanded by remember {
        mutableStateOf(false)
    }
    Column {
        OutlinedTextField(value = state.name, onValueChange = {
            onNameChange(it)
        })

        DropdownMenu(expanded = dropDownExpanded, onDismissRequest = { dropDownExpanded = false }) {
            OutlinedTextField(value = state.category.name, onValueChange = {
                onCategoryChange(state.category.copy(name = it))
            })
            selectableCategories.forEach {
                DropdownMenuItem(text = {
                    Text(it.name)
                }, onClick = { onCategoryChange(it) })
            }
        }
        val selectImage =
            rememberLauncherForActivityResult(contract = PickDocumentIntent()) {
                onCoverChange(it)
            }
        TextButton(onClick = { selectImage.launch(Unit) }) {
            Text(text = "Pick cover")
        }
        Row {
            state.coverUri?.let {
                CoilImage(
                    imageModel = {
                        it
                    }, modifier = Modifier
                        .heightIn(0.dp, 180.dp)
                )
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f))
                IconButton({
                    onCoverChange(null)
                }) {
                    Icon(Icons.Default.Remove, "Remove")

                }
            }
        }

    }
}


class PickDocumentIntent(val mime: String = "*/*") : ActivityResultContract<Unit, Uri?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mime
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode != Activity.RESULT_OK) return null
        return intent?.data

    }

}