package camiloo.com.example.volleyvision

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import camiloo.com.example.volleyvision.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    clubName: String = "CV OLÍMPICO",
    matchesList: List<MatchEvent> = emptyList(),
    playerList: List<PlayerMember> = emptyList(),
    onNavigateToNewMatch: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

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
fun CoachTacticsTab() {
    var selectedTab by remember { mutableStateOf("K1") }
    var selectedRotation by remember { mutableStateOf("Rotación 1 (P1)") }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Cabecera Táctica
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📐 Pizarra Táctica 2D", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Surface(
                color = Color(0xFF065F46),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "● MODO EDICIÓN",
                    color = Color(0xFF34D399),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filtros de Fase (K1 / K2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { selectedTab = "K1" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "K1") Color(0xFF065F46) else CardBg
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Fase K1 (Recep/Ataque)",
                    color = if (selectedTab == "K1") NeonGreen else TextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { selectedTab = "K2" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "K2") Color(0xFF065F46) else CardBg
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Fase K2 (Bloq/Def)",
                    color = if (selectedTab == "K2") NeonGreen else TextGray,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Selector de Rotaciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Rotación 1 (P1)", "Rotación 2", "Rotación 3").forEach { rot ->
                Surface(
                    color = if (selectedRotation == rot) Color(0xFF065F46) else CardBg,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedRotation == rot) NeonGreen else CardBorder),
                    modifier = Modifier.clickable { selectedRotation = rot }
                ) {
                    Text(
                        text = rot,
                        color = if (selectedRotation == rot) TextWhite else TextGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Campo Táctico 2D con delimitación para arrastrar libremente las fichas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .background(Color(0xFFD97706), shape = RoundedCornerShape(14.dp))
                .border(2.dp, Color.White, shape = RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .padding(10.dp)
        ) {
            // Red Central
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Color.White)
                    .align(Alignment.TopCenter)
            )

            // Fichas de Jugadores interactivas (arrastrables)
            TacticalPlayerToken("10", "PUNTA", initialOffset = Offset(20f, 40f))
            TacticalPlayerToken("7", "CENTRAL", initialOffset = Offset(130f, 40f))
            TacticalPlayerToken("4", "COLOC", initialOffset = Offset(240f, 40f))
            TacticalPlayerToken("2", "LÍBERO", initialOffset = Offset(20f, 180f))
            TacticalPlayerToken("9", "RECEP 2", initialOffset = Offset(130f, 220f))
            TacticalPlayerToken("15", "OPUESTO", initialOffset = Offset(240f, 180f))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Barra de Herramientas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg, shape = RoundedCornerShape(12.dp))
                .border(1.dp, CardBorder, shape = RoundedCornerShape(12.dp))
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TacticalToolItem("🖐️ Mover", isSelected = true)
            TacticalToolItem("📈 Ataque", isSelected = false)
            TacticalToolItem("🏐 Balón", isSelected = false)
            TacticalToolItem("⌫ Borrar", isSelected = false)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = { /* Lógica para guardar la jugada */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("💾 Guardar Jugada Táctica", color = BgMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun TacticalPlayerToken(
    number: String,
    role: String,
    initialOffset: Offset = Offset.Zero
) {
    var offset by remember { mutableStateOf(initialOffset) }

    Column(
        modifier = Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offset = Offset(
                        x = (offset.x + dragAmount.x).coerceAtLeast(0f),
                        y = (offset.y + dragAmount.y).coerceAtLeast(0f)
                    )
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .border(2.dp, Color(0xFF38BDF8), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Surface(
            color = Color.Black.copy(alpha = 0.75f),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                role,
                color = Color.White,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun TacticalToolItem(label: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) Color(0xFF065F46) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) NeonGreen else TextGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
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

// -------------------------------------------------------------
// VISTA PREVIA PARA ANDROID STUDIO
// -------------------------------------------------------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    VolleyVisionTheme {
        HomeScreen(
            clubName = "CV OLÍMPICO",
            matchesList = listOf(
                MatchEvent("DRAKOS", "Hoy, 18:30h", "Pabellón Norte", "Liga Oficial")
            )
        )
    }
}