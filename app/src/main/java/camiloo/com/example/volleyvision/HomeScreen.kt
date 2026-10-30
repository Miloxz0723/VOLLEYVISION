package camiloo.com.example.volleyvision

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
import camiloo.com.example.volleyvision.ui.theme.*

@Composable
fun HomeScreen(
    clubName: String = "CV OLÍMPICO",
    matchesList: List<MatchEvent> = emptyList(),
    playerList: List<PlayerMember> = emptyList(),
    onNavigateToNewMatch: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            Surface(
                color = CardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavItem(icon = "🏐", label = "Inicio", isSelected = selectedTab == 0) { selectedTab = 0 }
                    NavItem(icon = "📹", label = "Grabar", isSelected = selectedTab == 1) {
                        selectedTab = 1
                        onNavigateToNewMatch()
                    }
                    NavItem(icon = "📐", label = "Pizarra 2D", isSelected = selectedTab == 2) { selectedTab = 2 }
                    NavItem(icon = "📊", label = "Estadísticas", isSelected = selectedTab == 3) { selectedTab = 3 }
                    NavItem(icon = "👥", label = "Club", isSelected = selectedTab == 4) { selectedTab = 4 }
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

            // Cabecera Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ButtonDarkBg)
                            .border(1.dp, NeonGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏐", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("VolleyApp ", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(NeonGreen)
                            )
                        }
                        Text(clubName, color = Color(0xFF64B5F6), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ButtonDarkBg)
                        .border(1.dp, CardBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedTab) {
                0 -> CoachHomeTab(clubName = clubName, matchesList = matchesList, onNavigateToNewMatch = onNavigateToNewMatch)
                2 -> CoachTacticsTab()
                3 -> CoachStatsTab()
                4 -> CoachClubTab(clubName = clubName, playerList = playerList)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CoachHomeTab(clubName: String, matchesList: List<MatchEvent>, onNavigateToNewMatch: () -> Unit) {
    Column {
        Surface(
            color = NeonGreen,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToNewMatch() }
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("+ NUEVO PARTIDO", color = BgMain, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Agenda o inicia un partido para tu equipo",
                        color = BgMain.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("PRÓXIMOS PARTIDOS AGENDADOS", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (matchesList.isEmpty()) {
            Surface(
                color = CardBg,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "No hay partidos agendados aún. Presiona '+ NUEVO PARTIDO' para agendar uno.",
                    color = TextGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            matchesList.forEach { match ->
                Surface(
                    color = CardBg,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(match.matchType, color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(match.date, color = TextGray, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("$clubName vs ${match.rivalName}", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("📍 ${match.location}", color = TextGray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CoachClubTab(clubName: String, playerList: List<PlayerMember>) {
    Column {
        Text("👥  PLANTILLA DEL CLUB", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Gestión de la nómina de jugadores de $clubName", color = TextGray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))

        if (playerList.isEmpty()) {
            Surface(
                color = CardBg,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "No hay jugadores registrados aún en $clubName.",
                    color = TextGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            playerList.forEach { player ->
                PlayerListItem(dorsal = player.dorsal, name = player.name, position = player.position, isTitular = player.isTitular)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CoachTacticsTab() {
    Column {
        Text("📐  PIZARRA TÁCTICA 2D", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFF1B3B2B), shape = RoundedCornerShape(12.dp))
                .border(2.dp, NeonGreen, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("SISTEMA 5-1 ACTIVO", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CoachStatsTab() {
    Column {
        Text("📊  ESTADÍSTICAS DEL EQUIPO", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NavItem(icon: String, label: String, isSelected: Boolean, onClick: () -> Unit) {
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

@Composable
fun PlayerListItem(dorsal: String, name: String, position: String, isTitular: Boolean) {
    Surface(
        color = CardBg,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ButtonDarkBg)
                        .border(1.dp, NeonGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(dorsal, color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(name, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(position, color = TextGray, fontSize = 11.sp)
                }
            }

            if (isTitular) {
                Surface(
                    color = Color(0xFF00381B),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("REGISTRADO", color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }
    }
}