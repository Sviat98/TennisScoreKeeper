package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.file.domain.ExcelFile
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.open_file_storage
import tennisscorekeeper.composeapp.generated.resources.upload_file

@Composable
fun UploadFileComponent(
    modifier: Modifier = Modifier,
    file: ExcelFile,
    onFileStorageOpen: ()-> Unit,
    onUploadFile: ()-> Unit
){
    val fileName = file.name
    Row(
        modifier = Modifier.then(modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onFileStorageOpen) {
            Text(stringResource(Res.string.open_file_storage))
        }
        if(fileName.isNotBlank()){
            Text(fileName)
            Button(
                onClick = onUploadFile,
            ) {
                Text(stringResource(Res.string.upload_file))
            }
        }
    }
}