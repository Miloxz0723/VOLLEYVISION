package camiloo.com.example.volleyvision

import com.google.firebase.firestore.FirebaseFirestore

class FirebaseService {
    private val db = FirebaseFirestore.getInstance()

    // 1. Guardar/Actualizar un partido en Firestore usando su ID
    fun saveMatch(match: MatchEvent, onComplete: (Boolean) -> Unit = {}) {
        db.collection("matches")
            .document(match.id)
            .set(match)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // 2. Escuchar cambios de partidos en TIEMPO REAL
    fun listenToMatches(onMatchesUpdated: (List<MatchEvent>) -> Unit) {
        db.collection("matches")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val matches = snapshot.toObjects(MatchEvent::class.java)
                onMatchesUpdated(matches)
            }
    }

    // 3. Registrar un jugador en Firestore usando su ID
    fun savePlayer(player: PlayerMember, onComplete: (Boolean) -> Unit = {}) {
        db.collection("players")
            .document(player.id)
            .set(player)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // 4. Escuchar la plantilla de jugadores en TIEMPO REAL
    fun listenToPlayers(onPlayersUpdated: (List<PlayerMember>) -> Unit) {
        db.collection("players")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val players = snapshot.toObjects(PlayerMember::class.java)
                onPlayersUpdated(players)
            }
    }

    // 5. Guardar/Actualizar perfil de usuario en Firestore
    fun saveUserProfile(name: String, club: String, role: String, onComplete: (Boolean) -> Unit = {}) {
        val userMap = hashMapOf(
            "name" to name,
            "club" to club,
            "role" to role
        )

        db.collection("users")
            .document("current_user")
            .set(userMap)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // 6. Escuchar los datos del usuario en TIEMPO REAL
    fun listenToUserProfile(onUserUpdated: (String, String, String) -> Unit) {
        db.collection("users")
            .document("current_user")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                val name = snapshot.getString("name") ?: ""
                val club = snapshot.getString("club") ?: ""
                val role = snapshot.getString("role") ?: ""
                onUserUpdated(name, club, role)
            }
    }
}