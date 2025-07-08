package com.bashkevich.tennisscorekeeper.screens.participantlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.UploadFileComponent
import com.bashkevich.tennisscorekeeper.components.participant.ParticipantCard
import com.bashkevich.tennisscorekeeper.model.file.domain.ExcelFile
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.TournamentState
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.TournamentUiEvent
import com.mohamedrejeb.calf.core.LocalPlatformContext
import com.mohamedrejeb.calf.io.getName
import com.mohamedrejeb.calf.io.readByteArray
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import kotlinx.coroutines.launch

@Composable
fun ParticipantListScreen(
    modifier: Modifier = Modifier,
    state: TournamentState,
    onEvent: (TournamentUiEvent) -> Unit
) {

    ParticipantListContent(
        modifier = Modifier.then(modifier),
        state = state,
        onEvent = onEvent
    )
}

@Composable
fun ParticipantListContent(
    modifier: Modifier = Modifier,
    state: TournamentState,
    onEvent: (TournamentUiEvent) -> Unit
) {
    val participantListState = state.participantListState
    Scaffold(
        modifier = Modifier.then(modifier),
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Filled.Add, contentDescription = "Add Participant")
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val scope = rememberCoroutineScope()
            val context = LocalPlatformContext.current

            val excelPickerLauncher = rememberFilePickerLauncher(
                type = FilePickerFileType.Spreadsheet,
                selectionMode = FilePickerSelectionMode.Single,
                onResult = { files ->
                    scope.launch {
                        files.firstOrNull()?.let { file ->
                            // Do something with the selected file
                            // You can get the ByteArray of the file
                            val excelFile = ExcelFile(
                                name = file.getName(context) ?: "participants.xlsx",
                                content = file.readByteArray(context)
                            )
                            onEvent(TournamentUiEvent.SelectFile(excelFile))
                        }
                    }
                }
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.tournament.status == TournamentStatus.NOT_STARTED){
                    UploadFileComponent(
                        modifier = Modifier.fillMaxWidth(),
                        file = participantListState.participantsFile,
                        onFileStorageOpen = {
                            excelPickerLauncher.launch()
                        },
                        onUploadFile = {
                            onEvent(TournamentUiEvent.UploadFile)
                        }
                    )
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(
                        participantListState.participants,
                        key = { it.id }) { participant ->
                        ParticipantCard(participant = participant)
                    }
                }
            }
        }
    }
}