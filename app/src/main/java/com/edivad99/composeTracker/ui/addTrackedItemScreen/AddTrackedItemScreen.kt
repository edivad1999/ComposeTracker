package com.edivad99.composeTracker.ui.addTrackedItemScreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.edivad99.composeTracker.theme.components.OutlinedTextField
import com.edivad99.composeTracker.theme.components.TextButton
import com.skydoves.landscapist.coil.CoilImage
import domain.Category
import domain.DataResponse
import domain.TrackedItem
import org.koin.androidx.compose.get
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
                        onCategoryChange = model::onCategoryChange,
                        onCancel = onDismissRequest,
                        onConfirm = {
                            model.addTrackedItem(onDismissRequest)
                        }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun InsertData(
    state: TrackedItemState,
    selectableCategories: List<Category>,
    onNameChange: (String) -> Unit,
    onCoverChange: (Uri?) -> Unit,
    onCategoryChange: (Category) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    var dropDownExpanded by remember {
        mutableStateOf(false)
    }
    Column {
        Column(
            Modifier
                .heightIn(0.dp, 400.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = state.name, onValueChange = {
                    onNameChange(it)
                }, label = "Name", placeholder = "Name"
            )

            ExposedDropdownMenuBox(
                expanded = dropDownExpanded,
                onExpandedChange = { dropDownExpanded = it }) {
                OutlinedTextField(
                    value = state.category.name, onValueChange = {
                        onCategoryChange(state.category.copy(name = it))
                    }, label = "Category", modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = dropDownExpanded,
                    onDismissRequest = {
                        dropDownExpanded = false
                    },
                ) {
                    selectableCategories.forEach {
                        DropdownMenuItem(text = {
                            Text(it.name)
                        }, onClick = {
                            dropDownExpanded = false
                            onCategoryChange(it)
                        })
                    }
                }
            }
            val selectImage =
                rememberLauncherForActivityResult(contract = PickDocumentIntent(mime = "image/*")) {
                    onCoverChange(it)
                }
            TextButton(text = "Pick cover", onClick = { selectImage.launch(Unit) })
            Row {
                state.coverUri?.let {
                    CoilImage(
                        imageModel = {
                            it
                        }, modifier = Modifier.heightIn(0.dp, 180.dp)
                    )
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    IconButton({
                        onCoverChange(null)
                    }) {
                        Icon(Icons.Default.Remove, "Remove")

                    }
                }
            }

        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(text = "Cancel", onClick = onCancel)
            TextButton(
                enabled = state.name.isNotEmpty(),
                text = "Add", onClick = onConfirm
            )

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