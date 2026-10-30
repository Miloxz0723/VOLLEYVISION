package camiloo.com.example.volleyvision

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {
    var userName = mutableStateOf("Mateo Silva")
    var clubName = mutableStateOf("CV OLÍMPICO")
    var userRole = mutableStateOf("Entrenador")

    // Lista dinámica de partidos agendados
    val matchesList = mutableStateListOf<MatchEvent>()

    // Lista dinámica de la plantilla del club
    val playerList = mutableStateListOf<PlayerMember>()

    fun addMatch(rival: String, date: String, location: String, type: String) {
        matchesList.add(0, MatchEvent(rivalName = rival, date = date, location = location, matchType = type))
    }

    fun setUserData(name: String, club: String, role: String) {
        if (name.isNotBlank()) userName.value = name
        if (club.isNotBlank()) clubName.value = club
        if (role.isNotBlank()) userRole.value = role

        // Si se registra un jugador, lo añadimos dinámicamente a la plantilla
        if (role == "Jugador" && name.isNotBlank()) {
            playerList.add(0, PlayerMember(dorsal = "#${playerList.size + 1}", name = name, position = "Jugador"))
        }
    }
}

