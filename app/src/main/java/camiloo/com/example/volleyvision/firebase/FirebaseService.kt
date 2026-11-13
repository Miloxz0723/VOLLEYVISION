package camiloo.com.example.volleyvision.camiloo.com.example.volleyvision.firebase

import camiloo.com.example.volleyvision.MatchEvent
import camiloo.com.example.volleyvision.PlayerMember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions

class FirebaseService {

    // Instancias principales de Firebase: Autenticación y Base de Datos en la nube (Firestore)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Escuchadores en tiempo real para mantener sincronizados perfil, partidos y plantilla
    private var profileListener: ListenerRegistration? = null
    private var matchesListener: ListenerRegistration? = null
    private var playersListener: ListenerRegistration? = null

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    // Detiene todas las suscripciones activas en tiempo real (evita fugas de memoria al cerrar sesión)
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

    // Autenticación: Inicio de sesión con correo y contraseña
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

    // Registro: Crea el usuario en Firebase Auth y guarda su perfil (nombre, club y rol) en Firestore
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
                    "name" to name.trim(),
                    "club" to club.trim(),
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

    // Escucha en tiempo real los cambios en el perfil del usuario actual
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
            "name" to name.trim(),
            "club" to club.trim(),
            "role" to role
        )
        db.collection("users").document(uid).set(userData, SetOptions.merge())
    }

    // Sincronización por Rol: Si quien entra es "Jugador", busca el UID del Entrenador de su mismo club
    // para leer los partidos y la plantilla compartida; si es "Entrenador", usa su propio UID.
    private fun resolveClubOwnerUid(onResolved: (String?) -> Unit) {
        val uid = currentUserId ?: return onResolved(null)
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role") ?: "Entrenador"
                val club = doc.getString("club")?.trim() ?: ""
                if (role.equals("Jugador", ignoreCase = true) && club.isNotEmpty()) {
                    db.collection("users")
                        .whereEqualTo("club", club)
                        .whereEqualTo("role", "Entrenador")
                        .limit(1)
                        .get()
                        .addOnSuccessListener { coaches ->
                            val coachUid = coaches.documents.firstOrNull()?.id ?: uid
                            onResolved(coachUid)
                        }
                        .addOnFailureListener { onResolved(uid) }
                } else {
                    onResolved(uid)
                }
            }
            .addOnFailureListener { onResolved(uid) }
    }

    // Guarda o actualiza un partido en la subcolección "matches" del entrenador
    fun saveMatch(match: MatchEvent) {
        val uid = currentUserId ?: return
        db.collection("users")
            .document(uid)
            .collection("matches")
            .document(match.id)
            .set(match)
    }

    // Descarga y escucha en tiempo real todos los partidos del club (para entrenador y jugadores)
    fun listenToMatches(onMatchesReceived: (List<MatchEvent>) -> Unit) {
        matchesListener?.remove()
        resolveClubOwnerUid { targetUid ->
            val uid = targetUid ?: return@resolveClubOwnerUid
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
    }

    // Guarda o actualiza la ficha y estadísticas de un jugador en la subcolección "players"
    fun savePlayer(player: PlayerMember) {
        val uid = currentUserId ?: return
        db.collection("users")
            .document(uid)
            .collection("players")
            .document(player.id)
            .set(player)
    }

    // Descarga y escucha en tiempo real toda la plantilla y estadísticas del club
    fun listenToPlayers(onPlayersReceived: (List<PlayerMember>) -> Unit) {
        playersListener?.remove()
        resolveClubOwnerUid { targetUid ->
            val uid = targetUid ?: return@resolveClubOwnerUid
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
}