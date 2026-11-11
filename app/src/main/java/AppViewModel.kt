package camiloo.com.example.volleyvision

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.util.UUID

class AppViewModel : ViewModel() {

    private val firebaseService = FirebaseService()

    // Estados principales del usuario
    val userName = mutableStateOf("Entrenador")
    val clubName = mutableStateOf("CV OLÍMPICO")
    val userRole = mutableStateOf("Entrenador")

    // Listas observables en tiempo real
    val matchesList = mutableStateListOf<MatchEvent>()
    val playerList = mutableStateListOf<PlayerMember>()

    // Rotación táctica de posiciones en cancha (P1 a P6)
    val courtPositions = mutableStateMapOf<Int, PlayerMember?>()

    init {
        // Escuchar datos de Firebase Firestore al iniciar
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

    // 1. REGISTRO DE NUEVA CUENTA EN FIREBASE AUTH + FIRESTORE
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
            }
            onResult(success, error)
        }
    }

    // 2. ACTUALIZACIÓN MANUAL DE DATOS DE USUARIO
    fun setUserData(name: String, club: String, role: String) {
        userName.value = name
        clubName.value = club
        userRole.value = role
        firebaseService.saveUserProfile(name, club, role)
    }

    // 3. AGREGAR NUEVO PARTIDO Y GUARDAR EN FIRESTORE EN TIEMPO REAL
    fun addMatch(
        rival: String,
        date: String,
        location: String,
        matchType: String,
        registrationMode: String = "Solo Marcador y Puntos"
    ) {
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
            isFinished = false
        )

        // Agregar a la lista local observable
        matchesList.add(newMatch)

        // PERSISTIR DIRECTAMENTE EN FIRESTORE
        firebaseService.saveMatch(newMatch)
    }

    // 4. FINALIZAR PARTIDO Y ACTUALIZAR EN FIRESTORE
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
        val updatedStats = currentMatch.statsSummary.copy(
            attackPoints = attackPts,
            blockPoints = blockPts,
            servePoints = servePts
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

    // 5. REGISTRO DE PUNTOS POR ACCIÓN TÁCTICA
    fun recordPointForPlayerByAction(action: String) {
        val targetZone = when (action) {
            "SAQUE" -> 1
            "BLOQUEO" -> 3
            "REMATE" -> 4
            else -> 1
        }

        val playerInZone = courtPositions[targetZone]
        if (playerInZone != null) {
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
            courtPositions[targetZone] = updatedPlayer
            firebaseService.savePlayer(updatedPlayer)
        }
    }

    // 6. ROTACIÓN INICIAL EN CANCHA
    private fun setupInitialCourtRotation() {
        val titulars = playerList.filter { it.isTitular }.take(6)
        titulars.forEachIndexed { index, player ->
            courtPositions[index + 1] = player
        }
    }

    // CÁLCULO DE ESTADÍSTICAS
    fun getFinishedMatchesCount(): Int = matchesList.count { it.isFinished }

    fun getWinPercentage(): Int {
        val finished = matchesList.filter { it.isFinished }
        if (finished.isEmpty()) return 0
        val wins = finished.count { it.homeSets > it.guestSets }
        return ((wins.toFloat() / finished.size) * 100).toInt()
    }

    fun getAverageAttackEfficiency(): Float {
        if (playerList.isEmpty()) return 0.70f
        val totalAttacks = playerList.sumOf { it.stats.attackPoints }
        return if (totalAttacks == 0) 0.65f else (totalAttacks.toFloat() / (totalAttacks + 5)).coerceIn(0f, 1f)
    }

    fun getAverageServeEfficiency(): Float {
        if (playerList.isEmpty()) return 0.80f
        val totalServes = playerList.sumOf { it.stats.acePoints }
        return if (totalServes == 0) 0.75f else (totalServes.toFloat() / (totalServes + 5)).coerceIn(0f, 1f)
    }

    fun getAverageBlockEfficiency(): Float {
        if (playerList.isEmpty()) return 0.60f
        val totalBlocks = playerList.sumOf { it.stats.blockPoints }
        return if (totalBlocks == 0) 0.60f else (totalBlocks.toFloat() / (totalBlocks + 5)).coerceIn(0f, 1f)
    }

    fun getAverageReceptionEfficiency(): Float = 0.85f
}