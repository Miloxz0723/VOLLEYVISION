package camiloo.com.example.volleyvision

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Registrar Usuario en Auth y guardar perfil en Firestore
    fun registerUser(
        email: String,
        pass: String,
        name: String,
        club: String,
        role: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val userData = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "club" to club,
                    "role" to role,
                    "email" to email
                )
                db.collection("users").document(uid).set(userData)
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
            }
            .addOnFailureListener { e ->
                onResult(false, e.localizedMessage)
            }
    }

    // Escuchar cambios de datos de usuario
    fun listenToUserProfile(onUpdate: (String, String, String) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                val name = snapshot.getString("name") ?: "Entrenador"
                val club = snapshot.getString("club") ?: "CV OLÍMPICO"
                val role = snapshot.getString("role") ?: "Entrenador"
                onUpdate(name, club, role)
            }
        }
    }

    // Guardar o actualizar datos de usuario
    fun saveUserProfile(name: String, club: String, role: String) {
        val uid = auth.currentUser?.uid ?: return
        val userData = hashMapOf(
            "name" to name,
            "club" to club,
            "role" to role
        )
        db.collection("users").document(uid).set(userData, com.google.firebase.firestore.SetOptions.merge())
    }

    // GUARDAR O ACTUALIZAR UN PARTIDO EN LA COLECCIÓN "matches"
    fun saveMatch(match: MatchEvent) {
        db.collection("matches")
            .document(match.id)
            .set(match)
    }

    // Escuchar partidos en tiempo real
    fun listenToMatches(onMatchesReceived: (List<MatchEvent>) -> Unit) {
        db.collection("matches").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val list = snapshot.toObjects(MatchEvent::class.java)
                onMatchesReceived(list)
            }
        }
    }

    // GUARDAR JUGADOR EN LA COLECCIÓN "players"
    fun savePlayer(player: PlayerMember) {
        db.collection("players")
            .document(player.id)
            .set(player)
    }

    // Escuchar jugadores en tiempo real
    fun listenToPlayers(onPlayersReceived: (List<PlayerMember>) -> Unit) {
        db.collection("players").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val list = snapshot.toObjects(PlayerMember::class.java)
                onPlayersReceived(list)
            }
        }
    }
}