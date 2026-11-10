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

enum class Screen { WELCOME, LOGIN, REGISTER, HOME, NEW_MATCH, SCOREBOARD, PLAYER_HOME }

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VolleyVisionTheme {
                var currentScreen by rememberSaveable { mutableStateOf(Screen.WELCOME) }
                var currentRivalName by rememberSaveable { mutableStateOf("RIVAL") }

                when (currentScreen) {
                    Screen.WELCOME -> WelcomeScreen(
                        onNavigateToLogin = { currentScreen = Screen.LOGIN },
                        onNavigateToRegister = { currentScreen = Screen.REGISTER }
                    )
                    Screen.LOGIN -> LoginScreen(
                        onNavigateToRegister = { currentScreen = Screen.REGISTER },
                        onNavigateToHome = {
                            currentScreen = if (viewModel.userRole.value == "Jugador") Screen.PLAYER_HOME else Screen.HOME
                        },
                        onBackToWelcome = { currentScreen = Screen.WELCOME }
                    )
                    Screen.REGISTER -> RegisterScreen(
                        onNavigateToLogin = { currentScreen = Screen.LOGIN },
                        onRegisterSuccess = { name, club, role ->
                            viewModel.setUserData(name, club, role)
                            currentScreen = if (role == "Jugador") Screen.PLAYER_HOME else Screen.HOME
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
                        onNavigateToNewMatch = { currentScreen = Screen.NEW_MATCH }
                    )
                    Screen.NEW_MATCH -> NewMatchScreen(
                        clubName = viewModel.clubName.value,
                        onBackToHome = { currentScreen = Screen.HOME },
                        onMatchCreated = { rival, date, loc, type ->
                            viewModel.addMatch(rival, date, loc, type)
                            currentRivalName = if (rival.isBlank()) "VISITANTE" else rival
                            currentScreen = Screen.SCOREBOARD
                        }
                    )
                    Screen.SCOREBOARD -> ScoreboardScreen(
                        localTeamName = viewModel.clubName.value,
                        rivalName = currentRivalName,
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
                }
            }
        }
    }
}