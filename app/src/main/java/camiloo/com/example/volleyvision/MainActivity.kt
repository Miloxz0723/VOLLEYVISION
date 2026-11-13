package camiloo.com.example.volleyvision

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import camiloo.com.example.volleyvision.ui.theme.VolleyVisionTheme
import java.util.UUID

enum class Screen { WELCOME, LOGIN, REGISTER, HOME, NEW_MATCH, SCOREBOARD, PLAYER_HOME, ADD_PLAYER, CAMERA }

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VolleyVisionTheme {
                var currentScreen by rememberSaveable { mutableStateOf(Screen.WELCOME) }
                var currentRivalName by rememberSaveable { mutableStateOf("RIVAL") }
                var cameraReturnTo by rememberSaveable { mutableStateOf(Screen.HOME) }

                when (currentScreen) {
                    Screen.WELCOME -> WelcomeScreen(
                        onNavigateToLogin = { currentScreen = Screen.LOGIN },
                        onNavigateToRegister = { currentScreen = Screen.REGISTER }
                    )
                    Screen.LOGIN -> LoginScreen(
                        onNavigateToRegister = { currentScreen = Screen.REGISTER },
                        onLoginAttempt = { email, pass, onComplete ->
                            viewModel.loginAccount(email, pass, onComplete)
                        },
                        onNavigateToHome = {
                            currentScreen = if (viewModel.userRole.value == "Jugador") Screen.PLAYER_HOME else Screen.HOME
                        },
                        onBackToWelcome = { currentScreen = Screen.WELCOME }
                    )
                    Screen.REGISTER -> RegisterScreen(
                        onNavigateToLogin = { currentScreen = Screen.LOGIN },
                        onRegisterSuccess = { email, pass, name, club, role, onComplete ->
                            viewModel.registerNewAccount(email, pass, name, club, role) { success, err ->
                                onComplete(success, err)
                                if (success) {
                                    currentScreen = if (role == "Jugador") Screen.PLAYER_HOME else Screen.HOME
                                }
                            }
                        },
                        onBackToWelcome = { currentScreen = Screen.WELCOME }
                    )
                    Screen.HOME -> HomeScreen(
                        clubName = viewModel.clubName.value,
                        matchesList = viewModel.matchesList,
                        playerList = viewModel.playerList,
                        totalMatchesPlayed = viewModel.getFinishedMatchesCount(),
                        winPercentage = viewModel.getWinPercentage(),
                        attackEff = viewModel.getAverageAttackEfficiency(),
                        serveEff = viewModel.getAverageServeEfficiency(),
                        blockEff = viewModel.getAverageBlockEfficiency(),
                        receptionEff = viewModel.getAverageReceptionEfficiency(),
                        onNavigateToNewMatch = { currentScreen = Screen.NEW_MATCH },
                        onNavigateToCamera = {
                            cameraReturnTo = Screen.HOME
                            currentScreen = Screen.CAMERA
                        },
                        onAddPlayer = { newPlayer ->
                            viewModel.addPlayer(newPlayer)
                        }
                    )
                    Screen.NEW_MATCH -> NewMatchScreen(
                        clubName = viewModel.clubName.value,
                        onBackToHome = { currentScreen = Screen.HOME },
                        onMatchCreated = { rival, date, loc, type, mode ->
                            viewModel.addMatch(rival, date, loc, type, mode)
                            currentRivalName = if (rival.isBlank()) "VISITANTE" else rival

                            when {
                                // Solo Agendar Partido -> guarda limpio y vuelve al inicio
                                mode.contains("Agendar", ignoreCase = true) -> {
                                    currentScreen = Screen.HOME
                                }
                                // Grabar Video -> abre la cámara exclusivamente (vuelve a HOME al cerrar)
                                mode.contains("Grabar", ignoreCase = true) -> {
                                    cameraReturnTo = Screen.HOME
                                    currentScreen = Screen.CAMERA
                                }
                                // Solo Marcador y Puntos -> único modo que abre el marcador
                                else -> {
                                    currentScreen = Screen.SCOREBOARD
                                }
                            }
                        }
                    )
                    Screen.SCOREBOARD -> ScoreboardScreen(
                        localTeamName = viewModel.clubName.value,
                        rivalName = currentRivalName,
                        courtPositions = viewModel.courtPositions,
                        onPointScoredByAction = { action ->
                            viewModel.recordPointForPlayerByAction(action)
                        },
                        onGuestPointScored = {
                            viewModel.onGuestPointScored()
                        },
                        onUndoPointByAction = { action ->
                            viewModel.undoPointForPlayerByAction(action)
                        },
                        onFinishMatch = { homePoints, guestPoints, homeSets, guestSets, attackPts, blockPts, servePts ->
                            viewModel.finishCurrentMatch(
                                homePoints, guestPoints, homeSets, guestSets,
                                attackPts, blockPts, servePts
                            )
                            currentScreen = Screen.HOME
                        }
                    )
                    Screen.PLAYER_HOME -> PlayerHomeScreen(
                        playerName = viewModel.userName.value,
                        clubName = viewModel.clubName.value
                    )
                    Screen.ADD_PLAYER -> AddPlayerSheet(
                        onDismiss = { currentScreen = Screen.HOME },
                        onSave = { newPlayerForm ->
                            val newPlayer = PlayerMember(
                                id = UUID.randomUUID().toString(),
                                name = newPlayerForm.name,
                                dorsal = newPlayerForm.number.toString(),
                                position = newPlayerForm.position.label,
                                isTitular = true
                            )
                            viewModel.addPlayer(newPlayer)
                            currentScreen = Screen.HOME
                        }
                    )
                    Screen.CAMERA -> CameraScreen(
                        onClose = { currentScreen = cameraReturnTo }
                    )
                }
            }
        }
    }
}