package camiloo.com.example.volleyvision.pantalla.inicio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import camiloo.com.example.volleyvision.MatchEvent
import camiloo.com.example.volleyvision.PlayerMember
import camiloo.com.example.volleyvision.ui.theme.*

// Pantalla principal del rol Jugador con navegación inferior entre Mi Panel, Partidos y Equipo[cite: 9]
@Composable
fun PlayerHomeScreen(
    playerName: String = "Jugador",
    clubName: String = "CV OLÍMPICO",
    playerList: List<PlayerMember> = emptyList(),
    matchesList: List<MatchEvent> = emptyList(),
    attackEff: Float = 0f,
    serveEff: Float = 0f,
    blockEff: Float = 0f,
    receptionEff: Float = 0f,
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // Busca dentro de la plantilla del club el perfil que coincide con el nombre del jugador autenticado[cite: 9]
    val myPlayerProfile = playerList.firstOrNull {
        it.name.trim().equals(playerName.trim(), ignoreCase = true)
    } ?: playerList.firstOrNull {
        it.name.trim().contains(playerName.trim().substringBefore(" "), ignoreCase = true)
    }

    // Suma los puntos personales acumulados (remates, bloqueos y aces)[cite: 9]
    val totalPersonalPoints = (myPlayerProfile?.stats?.attackPoints ?: 0) +
            (myPlayerProfile?.stats?.blockPoints ?: 0) +
            (myPlayerProfile?.stats?.acePoints ?: 0)

    Scaffold(
        bottomBar = {
            Surface(
                color = CardBg,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayerNavItem(icon = "🏐", label = "Mi Panel", isSelected = selectedTab == 0) { selectedTab = 0 }
                    PlayerNavItem(icon = "📅", label = "Partidos", isSelected = selectedTab == 1) { selectedTab = 1 }
                    PlayerNavItem(icon = "👥", label = "Equipo", isSelected = selectedTab == 2) { selectedTab = 2 }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgMain)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Cabecera del jugador con su dorsal (#), nombre, posición táctica, club y botón de cierre de sesión[cite: 9]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ButtonDarkBg)
                            .border(2.dp, NeonGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = myPlayerProfile?.dorsal?.let { "#$it" } ?: "🏐",
                            color = NeonGreen,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(playerName, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${myPlayerProfile?.position ?: "Jugador Activo"} • $clubName",
                            color = Color(0xFF64B5F6),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                TextButton(onClick = onLogout) {
                    Text("Salir", color = Color(0xFFFF5252), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Contenido dinámico según la pestaña seleccionada en la barra inferior[cite: 9]
            when (selectedTab) {
                // Pestaña 1 (Mi Panel): Estadísticas individuales, próximo partido agendado y rendimiento global del club[cite: 9]
                0 -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("MIS ESTADÍSTICAS", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "TOTAL: $totalPersonalPoints PTS",
                            color = NeonGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PlayerStatBox(
                            title = "Remates",
                            value = "${myPlayerProfile?.stats?.attackPoints ?: 0}",
                            icon = "💥",
                            modifier = Modifier.weight(1f)
                        )
                        PlayerStatBox(
                            title = "Bloqueos",
                            value = "${myPlayerProfile?.stats?.blockPoints ?: 0}",
                            icon = "🛡️",
                            modifier = Modifier.weight(1f)
                        )
                        PlayerStatBox(
                            title = "Aces",
                            value = "${myPlayerProfile?.stats?.acePoints ?: 0}",
                            icon = "🚀",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Busca el encuentro pendiente más reciente programado por el entrenador[cite: 9]
                    val nextMatch = matchesList.lastOrNull { !it.isFinished }
                    Text("PRÓXIMO COMPROMISO", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = CardBg,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, if (nextMatch != null) NeonGreen else CardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (nextMatch != null) {
                                Text(nextMatch.matchType.uppercase(), color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$clubName vs ${nextMatch.rivalName}", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("📍 ${nextMatch.location} • 📅 ${nextMatch.date}", color = TextGray, fontSize = 12.sp)
                            } else {
                                Text("Sin partidos pendientes agendados por el entrenador.", color = TextGray, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("RENDIMIENTO DE $clubName", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = CardBg,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            PlayerSkillBar("Ataque del Equipo", attackEff)
                            PlayerSkillBar("Bloqueo del Equipo", blockEff)
                            PlayerSkillBar("Recepción", receptionEff)
                            PlayerSkillBar("Saque", serveEff)
                        }
                    }
                }

                // Pestaña 2 (Partidos): Calendario completo e historial de resultados del equipo[cite: 9]
                1 -> {
                    Text("CALENDARIO Y RESULTADOS", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (matchesList.isEmpty()) {
                        Surface(
                            color = CardBg,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, CardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Aún no hay partidos registrados en el club.",
                                color = TextGray,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        matchesList.forEach { match ->
                            Surface(
                                color = CardBg,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, CardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(match.matchType, color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = if (match.isFinished) "SETS ${match.homeSets}-${match.guestSets}" else "PENDIENTE",
                                            color = if (match.isFinished) Color(0xFF34D399) else Color(0xFFFBBF24),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("$clubName vs ${match.rivalName}", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text("📍 ${match.location} • 📅 ${match.date}", color = TextGray, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Pestaña 3 (Equipo): Plantilla completa de compañeros inscritos en el club y sus puntos totales[cite: 9]
                2 -> {
                    Text("PLANTILLA DE $clubName", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${playerList.size} jugadores inscritos en el club", color = TextGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (playerList.isEmpty()) {
                        Surface(
                            color = CardBg,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, CardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "El entrenador aún no ha registrado jugadores en la plantilla.",
                                color = TextGray,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        playerList.forEach { member ->
                            val pts = member.stats.attackPoints + member.stats.blockPoints + member.stats.acePoints
                            Surface(
                                color = CardBg,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, if (member.isTitular) NeonGreen.copy(alpha = 0.5f) else CardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(ButtonDarkBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("#${member.dorsal}", color = NeonGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(member.name, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = "${member.position} • ${if (member.isTitular) "Titular" else "Suplente"}",
                                                color = TextGray,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Text("$pts pts", color = NeonGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Componentes reutilizables para tarjetas de estadísticas, barras de progreso e ítems de navegación[cite: 9]
@Composable
private fun PlayerStatBox(title: String, value: String, icon: String, modifier: Modifier = Modifier) {
    Surface(
        color = CardBg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, CardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = NeonGreen, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text(title, color = TextGray, fontSize = 11.sp)
        }
    }
}

@Composable
private fun PlayerSkillBar(label: String, progress: Float) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = TextWhite, fontSize = 12.sp)
            Text("${(progress * 100).toInt()}%", color = TextGray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = NeonGreen,
            trackColor = Color(0xFF2C2C2C)
        )
    }
}

@Composable
private fun PlayerNavItem(icon: String, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) NeonGreen else TextGray,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

