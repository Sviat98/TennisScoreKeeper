package com.bashkevich.tennisscorekeeper.screens.tournamentdetails

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.match.repository.MatchRepository
import com.bashkevich.tennisscorekeeper.model.participant.repository.ParticipantRepository
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import com.bashkevich.tennisscorekeeper.navigation.TournamentTab
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class RefreshTournamentDetailsUseCase(
    private val tournamentRepository: TournamentRepository,
    private val matchRepository: MatchRepository,
    private val participantRepository: ParticipantRepository
) {
    private val refreshTrigger =
        MutableSharedFlow<RefreshTournamentDetailsInfo>(replay = 1, extraBufferCapacity = 1)

    fun refresh(refreshInfo: RefreshTournamentDetailsInfo) {
        refreshTrigger.tryEmit(refreshInfo)
    }

    fun observeTournamentByIdFromNetwork(
        tournamentId: Int
    ): Flow<LoadResult<Unit, Throwable>?> = flow {
        emit(null)
        refreshTrigger
            .onStart {
                refreshTrigger.tryEmit(
                    RefreshTournamentDetailsInfo(
                        tournamentTab = TournamentTab.MATCHES,
                        updateTournamentHeader = true
                    )
                )
            }
            .filter { it.updateTournamentHeader }
            .collect {
                emit(null)
                tournamentRepository.fetchTournamentById(tournamentId)
                    .doOnSuccess { emit(LoadResult.Success(Unit)) }
                    .doOnError { emit(LoadResult.Error(it)) }
            }
    }

    fun observeMatchesListFromNetwork(
        tournamentId: Int
    ): Flow<LoadResult<Unit, Throwable>?> =
        flow {
            emit(null)
            refreshTrigger
                .filter { it.tournamentTab == TournamentTab.MATCHES }
                .collect {
                    emit(null)
                    matchRepository.fetchMatchesForTournament(tournamentId)
                        .doOnSuccess { emit(LoadResult.Success(Unit)) }
                        .doOnError { emit(LoadResult.Error(it)) }
                }
        }

    fun observeParticipantsFromNetwork(
        tournamentId: Int
    ): Flow<LoadResult<Unit, Throwable>?> =
        flow {
            emit(null)
            refreshTrigger
                .filter { it.tournamentTab == TournamentTab.PARTICIPANTS }
                .collect {
                    emit(null)
                    participantRepository.fetchParticipantsForTournament(tournamentId)
                        .doOnSuccess { emit(LoadResult.Success(Unit)) }
                        .doOnError { emit(LoadResult.Error(it)) }
                }
        }
}

data class RefreshTournamentDetailsInfo(
    val tournamentTab: TournamentTab,
    // при swipe-to-refresh обновляем ВЕСЬ экран (updateTournamentHeader = true)
    // при переключении вкладок - только саму вкладку (updateTournamentHeader = false)
    val updateTournamentHeader: Boolean
)
