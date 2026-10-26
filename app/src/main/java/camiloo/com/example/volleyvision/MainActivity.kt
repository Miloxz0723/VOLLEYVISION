package camiloo.com.example.volleyvision

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import camiloo.com.example.volleyvision.ui.theme.VolleyVisionTheme

// Enumeración de todas las pantallas de la aplicación
enum class Screen { WELCOME, LOGIN, REGISTER, HOME, NEW_MATCH }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VolleyVisionTheme {
                var currentScreen by remember { mutableStateOf(Screen.WELCOME) }

                when (currentScreen) {
                    Screen.WELCOME -> WelcomeScreen(
                        onNavigateToLogin = { currentScreen = Screen.LOGIN },
                        onNavigateToRegister = { currentScreen = Screen.REGISTER }
                    )
                    Screen.LOGIN -> LoginScreen(
                        onNavigateToRegister = { currentScreen = Screen.REGISTER },
                        onNavigateToHome = { currentScreen = Screen.HOME },
                        onBackToWelcome = { currentScreen = Screen.WELCOME }
                    )
                    Screen.REGISTER -> RegisterScreen(
                        onNavigateToLogin = { currentScreen = Screen.LOGIN },
                        onNavigateToHome = { currentScreen = Screen.HOME },
                        onBackToWelcome = { currentScreen = Screen.WELCOME }
                    )
                    Screen.HOME -> HomeScreen(
                        onNavigateToNewMatch = { currentScreen = Screen.NEW_MATCH }
                    )
                    Screen.NEW_MATCH -> NewMatchScreen(
                        onBackToHome = { currentScreen = Screen.HOME }
                    )
                }
            }
        }
    }
}