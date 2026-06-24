package com.bashkevich.tennisscorekeeper.screens.tournamentlist

import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.core.remote.doOnError
import com.bashkevich.tennisscorekeeper.core.remote.doOnSuccess
import com.bashkevich.tennisscorekeeper.model.tournament.repository.TournamentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class RefreshTournamentListScreenUseCase(
    private val tournamentRepository: TournamentRepository
) {
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    fun refreshTournaments() {
        println("refreshTournaments tryEmit!!!")
        refreshTrigger.tryEmit(Unit)
    }

    fun fetchTournamentsFlow(): Flow<LoadResult<Unit, Throwable>?> = flow {
        refreshTrigger.onStart { emit(Unit) }.collect {
            emit(null)

            tournamentRepository.fetchTournaments()
                .doOnSuccess {
                    emit(LoadResult.Success(Unit))
                }
                .doOnError {
                    emit(LoadResult.Error(it))
                }
        }
    }
}