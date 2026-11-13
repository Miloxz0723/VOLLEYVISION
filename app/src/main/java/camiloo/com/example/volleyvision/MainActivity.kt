package camiloo.com.example.volleyvision

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import camiloo.com.example.volleyvision.camiloo.com.example.volleyvision.logica.AppViewModel
import camiloo.com.example.volleyvision.pantalla.inicio.PlayerHomeScreen
import camiloo.com.example.volleyvision.ui.theme.VolleyVisionTheme
import java.util.UUID

// Enumeración de todas las rutas/pantallas principales de la aplicación
enum class Screen { WELCOME, LOGIN, REGISTER, HOME, NEW_MATCH, SCOREBOARD, PLAYER_HOME, ADD_PLAYER, CAMERA }

// Punto de entrada principal de VolleyVision: conecta el ViewModel con las pantallas de Jetpack Compose
class MainActivity : ComponentActivity() {

    // Instancia única del ViewModel que sobrevive a rotaciones de pantalla (como el marcador horizontal)
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VolleyVisionTheme {
                // Estados de navegación persistentes ante cambios de configuración
                var currentScreen by rememberSaveable { mutableStateOf(Screen.WELCOME) }
                var currentRivalName by rememberSaveable { mutableStateOf("RIVAL") }
                var cameraReturnTo by rememberSaveable { mutableStateOf(Screen.HOME) }

                // Controlador central de navegación: renderiza cada pantalla y conecta sus eventos con el ViewModel
                when (currentScreen) {
                    // 1. Bienvenida y flujos de Autenticación (Login y Registro con redirección según rol)
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

                    // 2. Panel principal del Entrenador (estadísticas globales, partidos, pizarra y gestión de plantilla)
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

                    // 3. Configuración de nuevo encuentro y enrutamiento según la modalidad seleccionada
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

                    // 4. Marcador en vivo con rotación automática 5-1, registro de acciones y cierre del partido
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

                    // 5. Panel exclusivo del Jugador (estadísticas personales, partidos y plantilla del club)
                    Screen.PLAYER_HOME -> PlayerHomeScreen(
                        playerName = viewModel.userName.value,
                        clubName = viewModel.clubName.value,
                        playerList = viewModel.playerList,
                        matchesList = viewModel.matchesList,
                        attackEff = viewModel.getAverageAttackEfficiency(),
                        serveEff = viewModel.getAverageServeEfficiency(),
                        blockEff = viewModel.getAverageBlockEfficiency(),
                        receptionEff = viewModel.getAverageReceptionEfficiency(),
                        onLogout = {
                            viewModel.logoutAccount()
                            currentScreen = Screen.WELCOME
                        }
                    )

                    // 6. Módulo de grabación de video con CameraX
                    Screen.CAMERA -> CameraScreen(
                        onClose = { currentScreen = cameraReturnTo }
                    )

                    else -> {}
                }
            }
        }
    }
}