package camiloo.com.example.volleyvision

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {

    private val firebaseService = FirebaseService()

    // Información del usuario y perfil
    var userName = mutableStateOf("Entrenador")
    var clubName = mutableStateOf("CV OLÍMPICO")
    var userRole = mutableStateOf("Entrenador")

    // Listas observables principales
    val matchesList = mutableStateListOf<MatchEvent>()
    val playerList = mutableStateListOf<PlayerMember>()
    val tacticalPlaysList = mutableStateListOf<TacticalPlay>()

    private var currentMatchId: String? = null

    init {
        // Escuchar datos de Firestore en tiempo real
        listenToFirebaseData()
    }

    private fun listenToFirebaseData() {
        // Escuchar perfil del usuario
        firebaseService.listenToUserProfile { name, club, role ->
            if (name.isNotBlank()) userName.value = name
            if (club.isNotBlank()) clubName.value = club
            if (role.isNotBlank()) userRole.value = role
        }

        // Escuchar partidos en tiempo real
        firebaseService.listenToMatches { updatedMatches ->
            matchesList.clear()
            matchesList.addAll(updatedMatches)
        }

        // Escuchar plantilla de jugadores en tiempo real
        firebaseService.listenToPlayers { updatedPlayers ->
            if (updatedPlayers.isNotEmpty()) {
                playerList.clear()
                playerList.addAll(updatedPlayers)
            } else {
                loadInitialMockPlayers()
            }
        }
    }

    private fun loadInitialMockPlayers() {
        val initialPlayers = listOf(
            PlayerMember(dorsal = "10", name = "Mateo López", position = "PUNTA", isTitular = true),
            PlayerMember(dorsal = "7", name = "Carlos Ruiz", position = "CENTRAL", isTitular = true),
            PlayerMember(dorsal = "4", name = "Santiago Gómez", position = "COLOCADOR", isTitular = true),
            PlayerMember(dorsal = "2", name = "Lucas Silva", position = "LÍBERO", isTitular = true),
            PlayerMember(dorsal = "9", name = "Andrés Morales", position = "RECEP 2", isTitular = true),
            PlayerMember(dorsal = "15", name = "Gabriel Torres", position = "OPUESTO", isTitular = true)
        )
        initialPlayers.forEach { addPlayer(it.dorsal, it.name, it.position, it.isTitular) }
    }

    fun setUserData(name: String, club: String, role: String) {
        userName.value = name
        clubName.value = club
        userRole.value = role
        firebaseService.saveUserProfile(name, club, role)
    }

    // -------------------------------------------------------------
    // GESTIÓN DE PARTIDOS CON FIREBASE
    // -------------------------------------------------------------
    fun addMatch(rival: String, date: String, loc: String, type: String) {
        val newMatch = MatchEvent(
            rivalName = rival,
            date = date,
            location = loc,
            matchType = type,
            isFinished = false
        )
        currentMatchId = newMatch.id
        firebaseService.saveMatch(newMatch)
    }

    fun finishCurrentMatch(
        homePoints: Int, guestPoints: Int, homeSets: Int, guestSets: Int,
        attackPts: Int, blockPts: Int, servePts: Int
    ) {
        val index = matchesList.indexOfFirst { it.id == currentMatchId }
        if (index != -1) {
            val totalPts = if (homePoints > 0) homePoints else 1

            val attackPercentage = (attackPts.toFloat() / totalPts).coerceIn(0.1f, 0.95f)
            val blockPercentage = (blockPts.toFloat() / totalPts).coerceIn(0.1f, 0.95f)
            val servePercentage = (servePts.toFloat() / totalPts).coerceIn(0.1f, 0.95f)

            val generatedStats = MatchStatsSummary(
                attackPoints = attackPts,
                blockPoints = blockPts,
                servePoints = servePts,
                attackEfficiencyPercentage = attackPercentage,
                blockEfficiencyPercentage = blockPercentage,
                serveEfficiencyPercentage = servePercentage,
                receptionEfficiencyPercentage = 0.82f
            )

            val updatedMatch = matchesList[index].copy(
                homePoints = homePoints,
                guestPoints = guestPoints,
                homeSets = homeSets,
                guestSets = guestSets,
                isFinished = true,
                statsSummary = generatedStats
            )

            // Enviar actualización directamente a Firestore
            firebaseService.saveMatch(updatedMatch)
        }
    }

    // -------------------------------------------------------------
    // CÁLCULO DE ESTADÍSTICAS DINÁMICAS
    // -------------------------------------------------------------
    fun getFinishedMatchesCount(): Int = matchesList.count { it.isFinished }

    fun getWinPercentage(): Int {
        val finished = matchesList.filter { it.isFinished }
        if (finished.isEmpty()) return 0
        val wins = finished.count { it.homeSets > it.guestSets }
        return ((wins.toFloat() / finished.size) * 100).toInt()
    }

    fun getAverageAttackEfficiency(): Float {
        val finished = matchesList.filter { it.isFinished }
        if (finished.isEmpty()) return 0.68f
        return finished.map { it.statsSummary.attackEfficiencyPercentage }.average().toFloat()
    }

    fun getAverageServeEfficiency(): Float {
        val finished = matchesList.filter { it.isFinished }
        if (finished.isEmpty()) return 0.82f
        return finished.map { it.statsSummary.serveEfficiencyPercentage }.average().toFloat()
    }

    fun getAverageBlockEfficiency(): Float {
        val finished = matchesList.filter { it.isFinished }
        if (finished.isEmpty()) return 0.60f
        return finished.map { it.statsSummary.blockEfficiencyPercentage }.average().toFloat()
    }

    fun getAverageReceptionEfficiency(): Float {
        val finished = matchesList.filter { it.isFinished }
        if (finished.isEmpty()) return 0.85f
        return finished.map { it.statsSummary.receptionEfficiencyPercentage }.average().toFloat()
    }

    // -------------------------------------------------------------
    // GESTIÓN DE PLANTILLA DE JUGADORES
    // -------------------------------------------------------------
    fun addPlayer(dorsal: String, name: String, position: String, isTitular: Boolean = true) {
        val player = PlayerMember(
            dorsal = dorsal,
            name = name,
            position = position,
            isTitular = isTitular
        )
        firebaseService.savePlayer(player)
    }

    fun saveTacticalPlay(play: TacticalPlay) {
        tacticalPlaysList.add(play)
    }
}