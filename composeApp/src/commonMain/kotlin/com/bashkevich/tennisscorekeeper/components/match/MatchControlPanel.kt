package com.bashkevich.tennisscorekeeper.components.match

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bashkevich.tennisscorekeeper.model.match.remote.body.ScoreType
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsState
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsUiEvent

//@Composable
//fun ParticipantsPointsControlPanel(modifier: Modifier = Modifier,
//                                   matchId: String,
//                      state: MatchDetailsState){
//    Column {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Button(
//                onClick = {
//                    onEvent(
//                        MatchDetailsUiEvent.UpdateScore(
//                            matchId = matchId,
//                            playerId = firstPlayerId,
//                            scoreType = ScoreType.POINT
//                        )
//                    )
//                },
//                enabled = !isWinnerInMatch
//            ) {
//                Text("Player 1 Point")
//            }
//            Button(
//                onClick = {
//                    onEvent(
//                        MatchDetailsUiEvent.UpdateScore(
//                            matchId = matchId,
//                            playerId = secondPlayerId,
//                            scoreType = ScoreType.POINT
//                        )
//                    )
//                },
//                enabled = !isWinnerInMatch
//            ) {
//                Text("Player 2 Point")
//            }
//        }
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Button(
//                onClick = {
//                    onEvent(
//                        MatchDetailsUiEvent.UpdateScore(
//                            matchId = matchId,
//                            playerId = firstPlayerId,
//                            scoreType = ScoreType.GAME
//                        )
//                    )
//                },
//                // нужно залочить, когда начался гейм, когда определился победитель и во время супер-тайбрейка
//                enabled = !isGameStarted && !isWinnerInMatch && !isSuperTiebreak
//            ) {
//                Text("Player 1 Game")
//            }
//            Button(
//                onClick = {
//                    onEvent(
//                        MatchDetailsUiEvent.UpdateScore(
//                            matchId = matchId,
//                            playerId = secondPlayerId,
//                            scoreType = ScoreType.GAME
//                        )
//                    )
//                },
//                // нужно залочить, когда начался гейм, когда определился победитель и во время супер-тайбрейка
//                enabled = !isGameStarted && !isWinnerInMatch && !isSuperTiebreak
//            ) {
//                Text("Player 2 Game")
//            }
//        }
//    }
//}
