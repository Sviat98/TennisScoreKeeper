package com.bashkevich.tennisscorekeeper.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Upload
import com.bashkevich.tennisscorekeeper.model.file.domain.EMPTY_EXCEL_FILE
import com.bashkevich.tennisscorekeeper.model.file.domain.ExcelFile
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.select_file_for_upload
import tennisscorekeeper.shared.generated.resources.upload

@Composable
fun UploadFileComponent(
    modifier: Modifier = Modifier,
    file: ExcelFile,
    onFileStorageOpen: () -> Unit,
    onUploadFile: () -> Unit,
    onClearFile: () -> Unit,
) {
    val fileName = file.name
    val hasFile = fileName.isNotBlank()

    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FileSelectionRow(
            fileName = fileName,
            placeholder = stringResource(Res.string.select_file_for_upload),
            onFileStorageOpen = onFileStorageOpen,
            onClearFile = onClearFile,
        )
        AnimatedVisibility(
            visible = hasFile,
            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
        ) {
            Button(
                onClick = onUploadFile,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Icon(
                    imageVector = IconGroup.Default.Upload,
                    contentDescription = stringResource(Res.string.upload),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(Res.string.upload))
            }
        }
    }
}

@Preview
@Composable
fun UploadFileComponentEmptyPreview() {
    MaterialTheme {
        Surface {
            UploadFileComponent(
                modifier = Modifier.padding(16.dp),
                file = EMPTY_EXCEL_FILE,
                onFileStorageOpen = {},
                onUploadFile = {},
                onClearFile = {},
            )
        }
    }
}

@Preview
@Composable
fun UploadFileComponentSelectedPreview() {
    MaterialTheme {
        Surface {
            UploadFileComponent(
                modifier = Modifier.padding(16.dp),
                file = ExcelFile(
                    name = "participants.xlsx",
                    content = ByteArray(0),
                ),
                onFileStorageOpen = {},
                onUploadFile = {},
                onClearFile = {},
            )
        }
    }
}
