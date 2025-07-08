package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.file.domain.ExcelFile

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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onFileStorageOpen) {
            Text("Open file storage")
        }
        if(fileName.isNotBlank()){
            Text(fileName, modifier = Modifier.width(100.dp))
        }else{
            Spacer(modifier = Modifier.width(100.dp))
        }
        Button(
            onClick = onUploadFile,
            enabled = fileName.isNotBlank()
        ) {
            Text("Upload file")
        }
    }
}