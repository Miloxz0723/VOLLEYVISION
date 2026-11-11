package camiloo.com.example.volleyvision

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val currentUserId: String?
        get() = auth.currentUser?.uid

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

    fun listenToUserProfile(onUpdate: (String, String, String) -> Unit) {
        val uid = currentUserId ?: return
        db.collection("users").document(uid).addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                val name = snapshot.getString("name") ?: "Entrenador"
                val club = snapshot.getString("club") ?: "CV OLÍMPICO"
                val role = snapshot.getString("role") ?: "Entrenador"
                onUpdate(name, club, role)
            }
        }
    }

    fun saveUserProfile(name: String, club: String, role: String) {
        val uid = currentUserId ?: return
        val userData = hashMapOf(
            "name" to name,
            "club" to club,
            "role" to role
        )
        db.collection("users").document(uid).set(userData, com.google.firebase.firestore.SetOptions.merge())
    }

    // Subcolección única del usuario: users/{UID}/matches
    fun saveMatch(match: MatchEvent) {
        val uid = currentUserId ?: return
        db.collection("users")
            .document(uid)
            .collection("matches")
            .document(match.id)
            .set(match)
    }

    fun listenToMatches(onMatchesReceived: (List<MatchEvent>) -> Unit) {
        val uid = currentUserId ?: return
        db.collection("users")
            .document(uid)
            .collection("matches")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(MatchEvent::class.java)
                    onMatchesReceived(list)
                }
            }
    }

    // Subcolección única del usuario: users/{UID}/players
    fun savePlayer(player: PlayerMember) {
        val uid = currentUserId ?: return
        db.collection("users")
            .document(uid)
            .collection("players")
            .document(player.id)
            .set(player)
    }

    fun listenToPlayers(onPlayersReceived: (List<PlayerMember>) -> Unit) {
        val uid = currentUserId ?: return
        db.collection("users")
            .document(uid)
            .collection("players")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(PlayerMember::class.java)
                    onPlayersReceived(list)
                }
            }
    }
}