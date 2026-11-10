package camiloo.com.example.volleyvision

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {

    private val firebaseService = FirebaseService()

    var userName = mutableStateOf("Entrenador")
    var clubName = mutableStateOf("CV OLÍMPICO")
    var userRole = mutableStateOf("Entrenador")

    val matchesList = mutableStateListOf<MatchEvent>()
    val playerList = mutableStateListOf<PlayerMember>()
    val tacticalPlaysList = mutableStateListOf<TacticalPlay>()

    // Posiciones activas en la cancha: Mapa de Zona (1 a 6) -> Jugador
    val courtPositions = mutableStateOf(mutableMapOf<Int, PlayerMember>())

    private var currentMatchId: String? = null

    init {
        listenToFirebaseData()
    }

    private fun listenToFirebaseData() {
        firebaseService.listenToUserProfile { name, club, role ->
            if (name.isNotBlank()) userName.value = name
            if (club.isNotBlank()) clubName.value = club
            if (role.isNotBlank()) userRole.value = role
        }

        firebaseService.listenToMatches { updatedMatches ->
            matchesList.clear()
            matchesList.addAll(updatedMatches)
        }

        firebaseService.listenToPlayers { updatedPlayers ->
            if (updatedPlayers.isNotEmpty()) {
                playerList.clear()
                playerList.addAll(updatedPlayers)
                setupInitialCourtRotation()
            } else {
                loadInitialMockPlayers()
            }
        }
    }

    // Ubica a los titulares en las zonas P1 a P6 al iniciar el partido
    private fun setupInitialCourtRotation() {
        val map = mutableMapOf<Int, PlayerMember>()
        val titulares = playerList.filter { it.isTitular }.take(6)
        val activeLineup = if (titulares.size == 6) titulares else playerList.take(6)

        activeLineup.forEachIndexed { index, player ->
            val zone = index + 1
            player.currentZone = zone
            map[zone] = player
        }
        courtPositions.value = map
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

    // -------------------------------------------------------------
    // ASIGNACIÓN AUTOMÁTICA DE PUNTOS SEGÚN LA ZONA DE LA JUGADA
    // -------------------------------------------------------------
    fun recordPointForPlayerByAction(action: String) {
        val map = courtPositions.value

        // Asignación táctica:
        // - SAQUE: Jugador en P1 (zona de servicio)
        // - BLOQUEO: Central en P3 (centro de la red)
        // - REMATE: Atacante en P4 (red izquierda)
        val targetZone = when (action) {
            "SAQUE" -> 1
            "BLOQUEO" -> 3
            "REMATE" -> 4
            else -> 1
        }

        val player = map[targetZone]
        if (player != null) {
            when (action) {
                "SAQUE" -> player.stats.acePoints++
                "BLOQUEO" -> player.stats.blockPoints++
                "REMATE" -> player.stats.attackPoints++
            }
            // Sincronizar actualización individual del jugador en Firestore
            firebaseService.savePlayer(player)
        }
    }

    // -------------------------------------------------------------
    // ROTACIÓN DE EQUIPO EN SENTIDO HORARIO (P1 -> P6 -> P5 -> P4 -> P3 -> P2 -> P1)
    // -------------------------------------------------------------
    fun rotateTeam() {
        val currentMap = courtPositions.value
        val newMap = mutableMapOf<Int, PlayerMember>()

        val p1 = currentMap[1]
        val p2 = currentMap[2]
        val p3 = currentMap[3]
        val p4 = currentMap[4]
        val p5 = currentMap[5]
        val p6 = currentMap[6]

        if (p1 != null) newMap[6] = p1
        if (p6 != null) newMap[5] = p6
        if (p5 != null) newMap[4] = p5
        if (p4 != null) newMap[3] = p4
        if (p3 != null) newMap[2] = p3
        if (p2 != null) newMap[1] = p2

        courtPositions.value = newMap
    }

    fun setUserData(name: String, club: String, role: String) {
        userName.value = name
        clubName.value = club
        userRole.value = role
        firebaseService.saveUserProfile(name, club, role)
    }

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

            val generatedStats = MatchStatsSummary(
                attackPoints = attackPts,
                blockPoints = blockPts,
                servePoints = servePts,
                attackEfficiencyPercentage = (attackPts.toFloat() / totalPts).coerceIn(0.1f, 0.95f),
                blockEfficiencyPercentage = (blockPts.toFloat() / totalPts).coerceIn(0.1f, 0.95f),
                serveEfficiencyPercentage = (servePts.toFloat() / totalPts).coerceIn(0.1f, 0.95f)
            )

            val updatedMatch = matchesList[index].copy(
                homePoints = homePoints,
                guestPoints = guestPoints,
                homeSets = homeSets,
                guestSets = guestSets,
                isFinished = true,
                statsSummary = generatedStats
            )

            firebaseService.saveMatch(updatedMatch)
        }
    }

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