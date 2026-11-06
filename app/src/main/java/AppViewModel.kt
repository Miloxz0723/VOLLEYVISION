package camiloo.com.example.volleyvision

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {
    private val firebaseService = FirebaseService()

    var userName = mutableStateOf("Mateo Silva")
    var clubName = mutableStateOf("CV OLÍMPICO")
    var userRole = mutableStateOf("Entrenador")

    val matchesList = mutableStateListOf<MatchEvent>()
    val playerList = mutableStateListOf<PlayerMember>()

    init {
        // Escucha de datos de usuario en tiempo real desde Firestore
        firebaseService.listenToUserProfile { name, club, role ->
            if (name.isNotBlank()) userName.value = name
            if (club.isNotBlank()) clubName.value = club
            if (role.isNotBlank()) userRole.value = role
        }

        // Escucha de partidos en tiempo real desde Firestore
        firebaseService.listenToMatches { updatedMatches ->
            matchesList.clear()
            matchesList.addAll(updatedMatches)
        }

        // Escucha de plantilla de jugadores en tiempo real desde Firestore
        firebaseService.listenToPlayers { updatedPlayers ->
            playerList.clear()
            playerList.addAll(updatedPlayers)
        }
    }

    fun addMatch(rival: String, date: String, location: String, type: String) {
        val newMatch = MatchEvent(
            rivalName = rival,
            date = date,
            location = location,
            matchType = type
        )
        firebaseService.saveMatch(newMatch) { /* Guardado en colección 'matches' */ }
    }

    fun setUserData(name: String, club: String, role: String) {
        if (name.isNotBlank()) userName.value = name
        if (club.isNotBlank()) clubName.value = club
        if (role.isNotBlank()) userRole.value = role

        // Guardado persistente del usuario en Firestore
        firebaseService.saveUserProfile(
            name = userName.value,
            club = clubName.value,
            role = userRole.value
        ) { /* Guardado en colección 'users' */ }

        // Si el rol es Jugador, se agrega también a la plantilla general
        if (role == "Jugador" && name.isNotBlank()) {
            val newPlayer = PlayerMember(
                dorsal = "#${playerList.size + 1}",
                name = name,
                position = "Jugador"
            )
            firebaseService.savePlayer(newPlayer) { /* Guardado en colección 'players' */ }
        }
    }
}

