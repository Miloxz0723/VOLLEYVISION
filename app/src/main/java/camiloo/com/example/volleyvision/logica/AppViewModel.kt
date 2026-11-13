package camiloo.com.example.volleyvision.camiloo.com.example.volleyvision.logica

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import camiloo.com.example.volleyvision.MatchEvent
import camiloo.com.example.volleyvision.MatchStatsSummary
import camiloo.com.example.volleyvision.PlayerMember
import camiloo.com.example.volleyvision.camiloo.com.example.volleyvision.firebase.FirebaseService
import java.util.UUID

class AppViewModel : ViewModel() {

    private val firebaseService = FirebaseService()

    // Estados reactivos globales de la sesión actual (nombre, club y rol: Entrenador o Jugador)
    val userName = mutableStateOf("Entrenador")
    val clubName = mutableStateOf("CV OLÍMPICO")
    val userRole = mutableStateOf("Entrenador")

    // Listas dinámicas sincronizadas en tiempo real con Firebase para toda la interfaz
    val matchesList = mutableStateListOf<MatchEvent>()
    val playerList = mutableStateListOf<PlayerMember>()
    val courtPositions = mutableStateMapOf<Int, PlayerMember?>()

    // Rueda oficial de 6 jugadores en orden de rotación (índice 0 = Z1, 1 = Z6, 2 = Z5, 3 = Z4, 4 = Z3, 5 = Z2)
    private val rotationWheel = mutableListOf<PlayerMember?>()
    private var liberoPlayer: PlayerMember? = null

    val isHomeServing = mutableStateOf(true)

    init {
        loadUserData()
    }

    // Inicia la escucha en tiempo real de perfil, partidos y plantilla del club desde Firebase
    fun loadUserData() {
        matchesList.clear()
        playerList.clear()
        courtPositions.clear()
        rotationWheel.clear()
        liberoPlayer = null

        firebaseService.listenToUserProfile { name, club, role ->
            userName.value = name
            clubName.value = club
            userRole.value = role
        }

        firebaseService.listenToMatches { updatedMatches ->
            matchesList.clear()
            matchesList.addAll(updatedMatches)
        }

        firebaseService.listenToPlayers { updatedPlayers ->
            playerList.clear()
            playerList.addAll(updatedPlayers)
            setupInitialCourtRotation()
        }
    }

    // Gestión de autenticación: Iniciar sesión, cerrar sesión y registrar cuenta nueva
    fun loginAccount(
        email: String,
        pass: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        firebaseService.loginUser(email, pass) { success, error ->
            if (success) {
                loadUserData()
            }
            onResult(success, error)
        }
    }

    fun logoutAccount() {
        firebaseService.logout()
        userName.value = "Entrenador"
        clubName.value = "CV OLÍMPICO"
        userRole.value = "Entrenador"
        matchesList.clear()
        playerList.clear()
        courtPositions.clear()
        rotationWheel.clear()
        liberoPlayer = null
    }

    fun registerNewAccount(
        email: String,
        pass: String,
        name: String,
        club: String,
        role: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        firebaseService.registerUser(email, pass, name, club, role) { success, error ->
            if (success) {
                userName.value = name
                clubName.value = club
                userRole.value = role
                loadUserData()
            }
            onResult(success, error)
        }
    }

    fun setUserData(name: String, club: String, role: String) {
        userName.value = name
        clubName.value = club
        userRole.value = role
        firebaseService.saveUserProfile(name, club, role)
    }

    // Agrega un nuevo jugador a la plantilla, actualiza la cancha y lo guarda en Firestore
    fun addPlayer(newPlayer: PlayerMember) {
        playerList.add(newPlayer)
        setupInitialCourtRotation()
        firebaseService.savePlayer(newPlayer)
    }

    // Crea un nuevo partido (en vivo o agendado) y lo sincroniza en Firebase
    fun addMatch(
        rival: String,
        date: String,
        location: String,
        matchType: String,
        registrationMode: String = "Solo Marcador y Puntos"
    ) {
        val isScheduledOnly = registrationMode.contains("Agendar", ignoreCase = true)
        isHomeServing.value = true

        val newMatch = MatchEvent(
            id = UUID.randomUUID().toString(),
            rivalName = rival,
            date = date,
            location = location,
            matchType = matchType,
            registrationMode = registrationMode,
            homePoints = 0,
            guestPoints = 0,
            homeSets = 0,
            guestSets = 0,
            isFinished = false,
            statsSummary = if (isScheduledOnly) null else MatchStatsSummary()
        )

        matchesList.add(newMatch)
        firebaseService.saveMatch(newMatch)
    }

    // Cierra el partido en curso, calcula las eficiencias globales del equipo y guarda el resultado final
    fun finishCurrentMatch(
        homePoints: Int,
        guestPoints: Int,
        homeSets: Int,
        guestSets: Int,
        attackPts: Int,
        blockPts: Int,
        servePts: Int
    ) {
        val currentMatch = matchesList.lastOrNull { !it.isFinished } ?: return

        val totalAttacksCount = playerList.sumOf { it.stats.attackPoints }.coerceAtLeast(attackPts)
        val totalBlocksCount = playerList.sumOf { it.stats.blockPoints }.coerceAtLeast(blockPts)
        val totalAcesCount = playerList.sumOf { it.stats.acePoints }.coerceAtLeast(servePts)

        val totalScoredActions = (attackPts + blockPts + servePts).coerceAtLeast(1)
        val attEff = attackPts.toFloat() / totalScoredActions
        val blkEff = blockPts.toFloat() / totalScoredActions
        val srvEff = servePts.toFloat() / totalScoredActions

        val baseStats = currentMatch.statsSummary ?: MatchStatsSummary()
        val updatedStats = baseStats.copy(
            attackPoints = attackPts,
            blockPoints = blockPts,
            servePoints = servePts,
            totalAttacks = totalAttacksCount,
            totalBlocks = totalBlocksCount,
            totalAces = totalAcesCount,
            attackEfficiencyPercentage = attEff,
            blockEfficiencyPercentage = blkEff,
            serveEfficiencyPercentage = srvEff
        )

        val updatedMatch = currentMatch.copy(
            homePoints = homePoints,
            guestPoints = guestPoints,
            homeSets = homeSets,
            guestSets = guestSets,
            isFinished = true,
            statsSummary = updatedStats
        )

        val index = matchesList.indexOfFirst { it.id == currentMatch.id }
        if (index != -1) {
            matchesList[index] = updatedMatch
        }

        firebaseService.saveMatch(updatedMatch)
    }

    fun onGuestPointScored() {
        isHomeServing.value = false
    }

    // Registra un punto individual (Saque, Bloqueo o Remate) al jugador en su zona táctica
    // y ejecuta la rotación oficial si el equipo recupera el saque (side-out)
    fun recordPointForPlayerByAction(action: String) {
        if (courtPositions.isEmpty() && playerList.isNotEmpty()) {
            setupInitialCourtRotation()
        }

        // Si el rival tenía el saque y recuperamos el balón (side-out), rotamos 1 posición oficial
        if (!isHomeServing.value) {
            rotateCourt()
            isHomeServing.value = true
        }

        val targetZone = when (action) {
            "SAQUE" -> 1
            "BLOQUEO" -> 3
            "REMATE" -> 4
            else -> 1
        }

        val playerInZone = courtPositions[targetZone] ?: playerList.firstOrNull() ?: return

        val currentStats = playerInZone.stats
        val updatedStats = when (action) {
            "SAQUE" -> currentStats.copy(acePoints = currentStats.acePoints + 1)
            "BLOQUEO" -> currentStats.copy(blockPoints = currentStats.blockPoints + 1)
            "REMATE" -> currentStats.copy(attackPoints = currentStats.attackPoints + 1)
            else -> currentStats
        }
        val updatedPlayer = playerInZone.copy(stats = updatedStats)

        val index = playerList.indexOfFirst { it.id == playerInZone.id }
        if (index != -1) {
            playerList[index] = updatedPlayer
        }

        // Actualiza el jugador tanto en la rueda como en el mapa de cancha
        for (i in rotationWheel.indices) {
            if (rotationWheel[i]?.id == updatedPlayer.id) {
                rotationWheel[i] = updatedPlayer
            }
        }
        if (courtPositions[targetZone]?.id == updatedPlayer.id) {
            courtPositions[targetZone] = updatedPlayer
        }

        firebaseService.savePlayer(updatedPlayer)
    }

    // Deshace el último punto sumado por acción para corregir errores en el marcador en vivo
    fun undoPointForPlayerByAction(action: String) {
        val targetZone = when (action) {
            "SAQUE" -> 1
            "BLOQUEO" -> 3
            "REMATE" -> 4
            else -> 1
        }

        val playerInZone = courtPositions[targetZone] ?: playerList.firstOrNull() ?: return
        val currentStats = playerInZone.stats
        val updatedStats = when (action) {
            "SAQUE" -> currentStats.copy(acePoints = (currentStats.acePoints - 1).coerceAtLeast(0))
            "BLOQUEO" -> currentStats.copy(blockPoints = (currentStats.blockPoints - 1).coerceAtLeast(0))
            "REMATE" -> currentStats.copy(attackPoints = (currentStats.attackPoints - 1).coerceAtLeast(0))
            else -> currentStats
        }
        val updatedPlayer = playerInZone.copy(stats = updatedStats)

        val index = playerList.indexOfFirst { it.id == playerInZone.id }
        if (index != -1) {
            playerList[index] = updatedPlayer
        }
        for (i in rotationWheel.indices) {
            if (rotationWheel[i]?.id == updatedPlayer.id) {
                rotationWheel[i] = updatedPlayer
            }
        }
        if (courtPositions[targetZone]?.id == updatedPlayer.id) {
            courtPositions[targetZone] = updatedPlayer
        }
        firebaseService.savePlayer(updatedPlayer)
    }

    // Arma la formación oficial 5-1 en rueda de 6 jugadores:
    // Orden de rotación: [Z1: Colocador, Z6: Punta 2, Z5: Central 2 (cubierto por Líbero), Z4: Punta 1, Z3: Central 1, Z2: Opuesto]
    // Así las puntas están opuestas (Z4 y Z6) y Levantador/Opuesto están opuestos (Z1 y Z2):
    // cuando una punta sube, la otra baja; cuando el levantador sube, el opuesto baja.
    fun setupInitialCourtRotation() {
        courtPositions.clear()
        rotationWheel.clear()
        if (playerList.isEmpty()) return

        val available = playerList.toMutableList()

        fun pickByRole(vararg keywords: String): PlayerMember? {
            val found = available.firstOrNull { player ->
                keywords.any { kw -> player.position.contains(kw, ignoreCase = true) }
            }
            if (found != null) available.remove(found)
            return found
        }

        val setter = pickByRole("Colocador", "Levantador", "Armador")
        val opposite = pickByRole("Opuesto")
        val punta1 = pickByRole("Punta", "Receptor", "Atacante")
        val punta2 = pickByRole("Punta", "Receptor", "Atacante")
        val central1 = pickByRole("Central")
        val central2 = pickByRole("Central")
        liberoPlayer = pickByRole("Líbero", "Libero")

        // Orden en rueda (6 posiciones):
        // [0] -> Z1 (Colocador)
        // [1] -> Z6 (Punta 2)
        // [2] -> Z5 (Central 2 / Líbero)
        // [3] -> Z4 (Punta 1)
        // [4] -> Z3 (Central 1)
        // [5] -> Z2 (Opuesto)
        val z1 = setter ?: available.removeFirstOrNull()
        val z6 = punta2 ?: available.removeFirstOrNull()
        val z5 = central2 ?: liberoPlayer ?: available.removeFirstOrNull()
        val z4 = punta1 ?: available.removeFirstOrNull()
        val z3 = central1 ?: available.removeFirstOrNull()
        val z2 = opposite ?: available.removeFirstOrNull()

        rotationWheel.addAll(listOf(z1, z6, z5, z4, z3, z2))
        syncWheelToCourtZones()
    }

    // Avanza 1 paso real en la rueda de 6 posiciones:
    // El jugador de Z2 baja a Z1 (saque), Z1 baja a Z6, Z6 pasa a Z5, Z5 sube a Z4, Z4 pasa a Z3, Z3 pasa a Z2.
    // De esta forma, en cada rotación una punta sube y la otra baja, y lo mismo ocurre entre levantador y opuesto.
    fun rotateCourt() {
        if (rotationWheel.size < 6) return
        // En sentido horario: quien estaba en Z2 (índice 5) pasa a Z1 (índice 0)
        val incomingToZ1 = rotationWheel.removeAt(5)
        rotationWheel.add(0, incomingToZ1)
        syncWheelToCourtZones()
    }

    // Mapea la rueda de 6 jugadores a las zonas 1..6 en cancha y aplica la regla táctica en juego:
    // - En Z4 (Remate) siempre ataca la Punta delantera o el delantero activo en Z4
    // - En Z3 (Bloqueo) siempre bloquea el Central delantero (si el central rotó a zaguero, entra el otro central adelante)
    // - En Z1 (Saque) saca quien está actualmente en Z1 tras la rotación
    // - En Z5 (Defensa) cubre el Líbero cuando está disponible
    private fun syncWheelToCourtZones() {
        if (rotationWheel.size < 6) return

        val z1 = rotationWheel[0] // Saque actual
        val z6 = rotationWheel[1] // Zaguero centro
        val z5 = liberoPlayer ?: rotationWheel[2] // Líbero en Z5
        val z4 = rotationWheel[3] // Delantero izquierdo (Remate)
        val z3 = rotationWheel[4] // Delantero centro (Bloqueo)
        val z2 = rotationWheel[5] // Delantero derecho

        courtPositions[1] = z1
        courtPositions[6] = z6
        courtPositions[5] = z5
        courtPositions[4] = z4
        courtPositions[3] = z3
        courtPositions[2] = z2
    }

    // Métricas y promedios estadísticos del equipo mostrados en los paneles de inicio
    fun getFinishedMatchesCount(): Int = matchesList.count { it.isFinished }

    fun getWinPercentage(): Int {
        val finished = matchesList.filter { it.isFinished }
        if (finished.isEmpty()) return 0
        val wins = finished.count { it.homeSets > it.guestSets }
        return ((wins.toFloat() / finished.size) * 100).toInt()
    }

    fun getAverageAttackEfficiency(): Float {
        val finishedWithStats = matchesList.filter { it.isFinished && it.statsSummary != null }
        if (finishedWithStats.isEmpty()) return 0f
        val avg = finishedWithStats.mapNotNull { it.statsSummary?.attackEfficiencyPercentage }.average().toFloat()
        return avg.coerceIn(0f, 1f)
    }

    fun getAverageServeEfficiency(): Float {
        val finishedWithStats = matchesList.filter { it.isFinished && it.statsSummary != null }
        if (finishedWithStats.isEmpty()) return 0f
        val avg = finishedWithStats.mapNotNull { it.statsSummary?.serveEfficiencyPercentage }.average().toFloat()
        return avg.coerceIn(0f, 1f)
    }

    fun getAverageBlockEfficiency(): Float {
        val finishedWithStats = matchesList.filter { it.isFinished && it.statsSummary != null }
        if (finishedWithStats.isEmpty()) return 0f
        val avg = finishedWithStats.mapNotNull { it.statsSummary?.blockEfficiencyPercentage }.average().toFloat()
        return avg.coerceIn(0f, 1f)
    }

    fun getAverageReceptionEfficiency(): Float {
        val finishedWithStats = matchesList.filter { it.isFinished && it.statsSummary != null }
        if (finishedWithStats.isEmpty()) return 0f
        val avg = finishedWithStats.mapNotNull { it.statsSummary?.receptionEfficiencyPercentage }.average().toFloat()
        return avg.coerceIn(0f, 1f)
    }
}