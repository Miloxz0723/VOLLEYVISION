package camiloo.com.example.volleyvision

data class MatchEvent(
    val rivalName: String,
    val date: String,
    val location: String,
    val matchType: String,
    var isConfirmed: Boolean = false
)

data class TrainingEvent(
    val dayTag: String,
    val title: String,
    val location: String,
    val duration: String
)

data class PlayerMember(
    val dorsal: String,
    val name: String,
    val position: String,
    val isTitular: Boolean = true
)