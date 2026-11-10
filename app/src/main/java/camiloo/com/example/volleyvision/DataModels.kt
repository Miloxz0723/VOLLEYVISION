package camiloo.com.example.volleyvision

enum class AppScreen {
    HOME,
    NEW_MATCH,
    SCOREBOARD,
    TACTICAL_BOARD
}

// 1. MODELO DE ESTADÍSTICAS DETALLADAS POR ACCIÓN
data class MatchStatsSummary(
    var attackPoints: Int = 0,
    var blockPoints: Int = 0,
    var servePoints: Int = 0,
    var totalAttacks: Int = 0,
    var totalBlocks: Int = 0,
    var totalAces: Int = 0,
    var attackEfficiencyPercentage: Float = 0.0f,
    var serveEfficiencyPercentage: Float = 0.0f,
    var blockEfficiencyPercentage: Float = 0.0f,
    var receptionEfficiencyPercentage: Float = 0.80f
)

// 2. MODELO DE PARTIDO E HISTORIAL
data class MatchEvent(
    var id: String = java.util.UUID.randomUUID().toString(),
    var rivalName: String = "",
    var date: String = "",
    var location: String = "",
    var matchType: String = "",
    var isConfirmed: Boolean = false,
    var registrationMode: String = "Solo Marcador y Puntos",
    var homePoints: Int = 0,
    var guestPoints: Int = 0,
    var homeSets: Int = 0,
    var guestSets: Int = 0,
    var isFinished: Boolean = false,
    var statsSummary: MatchStatsSummary = MatchStatsSummary()
)

// 3. MODELO DE JUGADOR / PLANTILLA
data class PlayerMember(
    var id: String = java.util.UUID.randomUUID().toString(),
    var dorsal: String = "",
    var name: String = "",
    var position: String = "",
    var isTitular: Boolean = true,
    var stats: PlayerStats = PlayerStats()
)

data class PlayerStats(
    var matchesPlayed: Int = 0,
    var attackPoints: Int = 0,
    var blockPoints: Int = 0,
    var acePoints: Int = 0,
    var serveErrors: Int = 0,
    var receptionErrors: Int = 0
)

// 4. MODELO DE JUGADA TÁCTICA 2D
data class TacticalPlay(
    var id: String = java.util.UUID.randomUUID().toString(),
    var title: String = "",
    var phase: String = "K1",
    var rotationName: String = "Rotación 1 (P1)",
    var positions: List<PlayerTokenPosition> = emptyList()
)

data class PlayerTokenPosition(
    var dorsal: String = "",
    var role: String = "",
    var xOffset: Float = 0f,
    var yOffset: Float = 0f
)