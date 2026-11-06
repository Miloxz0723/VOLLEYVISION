package camiloo.com.example.volleyvision

enum class AppScreen {
    HOME,
    NEW_MATCH,
    SCOREBOARD,
    TACTICAL_BOARD
}

data class MatchEvent(
    val rivalName: String = "",
    val date: String = "",
    val location: String = "",
    val matchType: String = "",
    var isConfirmed: Boolean = false,
    val registrationMode: String = "Solo Marcador y Puntos"
)

data class PlayerMember(
    val dorsal: String = "",
    val name: String = "",
    val position: String = "",
    val isTitular: Boolean = true
)