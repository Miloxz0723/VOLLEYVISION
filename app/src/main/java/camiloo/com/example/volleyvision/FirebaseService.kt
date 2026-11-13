package camiloo.com.example.volleyvision

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions

class FirebaseService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var profileListener: ListenerRegistration? = null
    private var matchesListener: ListenerRegistration? = null
    private var playersListener: ListenerRegistration? = null

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    fun clearListeners() {
        profileListener?.remove()
        matchesListener?.remove()
        playersListener?.remove()
        profileListener = null
        matchesListener = null
        playersListener = null
    }

    fun logout() {
        clearListeners()
        auth.signOut()
    }

    fun loginUser(
        email: String,
        pass: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        clearListeners()
        auth.signInWithEmailAndPassword(email.trim(), pass)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.localizedMessage)
            }
    }

    fun registerUser(
        email: String,
        pass: String,
        name: String,
        club: String,
        role: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        clearListeners()
        auth.createUserWithEmailAndPassword(email.trim(), pass)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val userData = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "club" to club,
                    "role" to role,
                    "email" to email.trim()
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
        profileListener?.remove()
        val uid = currentUserId ?: return
        profileListener = db.collection("users").document(uid).addSnapshotListener { snapshot, _ ->
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
        db.collection("users").document(uid).set(userData, SetOptions.merge())
    }

    fun saveMatch(match: MatchEvent) {
        val uid = currentUserId ?: return
        db.collection("users")
            .document(uid)
            .collection("matches")
            .document(match.id)
            .set(match)
    }

    fun listenToMatches(onMatchesReceived: (List<MatchEvent>) -> Unit) {
        matchesListener?.remove()
        val uid = currentUserId ?: return
        matchesListener = db.collection("users")
            .document(uid)
            .collection("matches")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(MatchEvent::class.java)
                    onMatchesReceived(list)
                }
            }
    }

    fun savePlayer(player: PlayerMember) {
        val uid = currentUserId ?: return
        db.collection("users")
            .document(uid)
            .collection("players")
            .document(player.id)
            .set(player)
    }

    fun listenToPlayers(onPlayersReceived: (List<PlayerMember>) -> Unit) {
        playersListener?.remove()
        val uid = currentUserId ?: return
        playersListener = db.collection("users")
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