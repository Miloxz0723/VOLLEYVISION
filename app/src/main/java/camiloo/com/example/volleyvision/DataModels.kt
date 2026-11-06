package camiloo.com.example.volleyvision

data class MatchEvent(
    val rivalName: String = "",
    val date: String = "",
    val location: String = "",
    val matchType: String = "",
    var isConfirmed: Boolean = false
)

data class PlayerMember(
    val dorsal: String = "",
    val name: String = "",
    val position: String = "",
    val isTitular: Boolean = true
)