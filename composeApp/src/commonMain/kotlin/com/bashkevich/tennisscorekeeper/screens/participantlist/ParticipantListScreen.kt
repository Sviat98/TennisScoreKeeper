package com.bashkevich.tennisscorekeeper.screens.participantlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.DefaultLoadingComponent
import com.bashkevich.tennisscorekeeper.components.UploadFileComponent
import com.bashkevich.tennisscorekeeper.components.participant.ParticipantListComponent
import com.bashkevich.tennisscorekeeper.model.file.domain.ExcelFile
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.ParticipantListLoadingState
import com.mohamedrejeb.calf.core.LocalPlatformContext
import com.mohamedrejeb.calf.io.getName
import com.mohamedrejeb.calf.io.readByteArray
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.couldnt_load_data
import tennisscorekeeper.composeapp.generated.resources.participant_list_empty
import tennisscorekeeper.composeapp.generated.resources.pull_down_to_update

@Composable
fun ParticipantListScreen(
    modifier: Modifier = Modifier,
    participantListLoadingState: ParticipantListLoadingState,
    tournamentStatus: TournamentStatus,
    onUploadFile: () -> Unit,
    onSelectFile: (ExcelFile) -> Unit
) {
    Box(modifier = modifier) {
        when (participantListLoadingState) {
            is ParticipantListLoadingState.Loading -> {
                // тут НЕ делаем verticalScroll, в состояни Loading refresh не должен быть доступен
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ParticipantListLoadingState.InitialError -> {
                Box(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                    ) {
                        Text(stringResource(Res.string.couldnt_load_data), color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            stringResource(Res.string.pull_down_to_update),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is ParticipantListLoadingState.Content -> {
                if (participantListLoadingState.isUploadInProgress) {
                    DefaultLoadingComponent(modifier = Modifier.fillMaxSize())
                } else {
                    ParticipantListContent(
                        modifier = Modifier.fillMaxSize(),
                        participantListLoadingState = participantListLoadingState,
                        tournamentStatus = tournamentStatus,
                        onUploadFile = onUploadFile,
                        onSelectFile = onSelectFile
                    )
                }
            }
        }
    }
}

@Composable
private fun ParticipantListContent(
    modifier: Modifier = Modifier,
    participantListLoadingState: ParticipantListLoadingState.Content,
    tournamentStatus: TournamentStatus,
    onUploadFile: () -> Unit,
    onSelectFile: (ExcelFile) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalPlatformContext.current

    val excelPickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Spreadsheet,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files ->
            scope.launch {
                files.firstOrNull()?.let { file ->
                    val excelFile = ExcelFile(
                        name = file.getName(context) ?: "participants.xlsx",
                        content = file.readByteArray(context)
                    )
                    onSelectFile(excelFile)
                }
            }
        }
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (participantListLoadingState.participants.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (tournamentStatus == TournamentStatus.NOT_STARTED) {
                    UploadFileComponent(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        file = participantListLoadingState.participantsFile,
                        onFileStorageOpen = { excelPickerLauncher.launch() },
                        onUploadFile = onUploadFile
                    )
                }

                Text(
                    stringResource(Res.string.participant_list_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            if (tournamentStatus == TournamentStatus.NOT_STARTED) {
                UploadFileComponent(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    file = participantListLoadingState.participantsFile,
                    onFileStorageOpen = {
                        excelPickerLauncher.launch()
                    },
                    onUploadFile = onUploadFile
                )
            }

            ParticipantListComponent(
                participants = participantListLoadingState.participants
            )
        }
    }
}
