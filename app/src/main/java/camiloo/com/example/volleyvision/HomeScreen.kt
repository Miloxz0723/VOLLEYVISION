package camiloo.com.example.volleyvision

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import camiloo.com.example.volleyvision.ui.theme.*
import kotlin.math.roundToInt

data class TacticalLinePath(val points: List<Offset>, val color: Color = Color.Yellow)

@Composable
fun HomeScreen(
    clubName: String = "CV OLÍMPICO",
    matchesList: List<MatchEvent> = emptyList(),
    playerList: List<PlayerMember> = emptyList(),
    totalMatchesPlayed: Int = 0,
    winPercentage: Int = 0,
    attackEff: Float = 0f,
    serveEff: Float = 0f,
    blockEff: Float = 0f,
    receptionEff: Float = 0f,
    onNavigateToNewMatch: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedMatchForDetails by remember { mutableStateOf<MatchEvent?>(null) }

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
                0 -> CoachHomeTab(
                    clubName = clubName,
                    matchesList = matchesList,
                    onNavigateToNewMatch = onNavigateToNewMatch,
                    onMatchClick = { match -> selectedMatchForDetails = match }
                )
                2 -> CoachTacticsTab()
                3 -> CoachStatsTab(
                    matchesPlayed = totalMatchesPlayed,
                    winRate = winPercentage,
                    attackEff = attackEff,
                    serveEff = serveEff,
                    blockEff = blockEff,
                    receptionEff = receptionEff
                )
                4 -> CoachClubTab(clubName = clubName, playerList = playerList)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal de Detalle del Partido
    selectedMatchForDetails?.let { match ->
        MatchDetailDialog(
            match = match,
            localClubName = clubName,
            onDismiss = { selectedMatchForDetails = null }
        )
    }
}

@Composable
fun CoachHomeTab(
    clubName: String,
    matchesList: List<MatchEvent>,
    onNavigateToNewMatch: () -> Unit,
    onMatchClick: (MatchEvent) -> Unit
) {
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

        Text("HISTORIAL Y PARTIDOS AGENDADOS", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (matchesList.isEmpty()) {
            Surface(
                color = CardBg,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "No hay partidos registrados aún. Presiona '+ NUEVO PARTIDO' para agendar uno.",
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
                        .clickable { onMatchClick(match) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(match.matchType, color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Surface(
                                color = if (match.isFinished) Color(0xFF2E7D32) else Color(0xFFF57C00),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (match.isFinished) "SETS: ${match.homeSets} - ${match.guestSets}" else "PENDIENTE",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("$clubName vs ${match.rivalName}", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("📍 ${match.location} | 📅 ${match.date}", color = TextGray, fontSize = 12.sp)
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

    // ESTADOS PARA HERRAMIENTAS INTERACTIVAS (BALÓN Y DIBUJO DE ATAQUE)
    var selectedToolIndex by remember { mutableIntStateOf(0) } // 0: Mover, 1: Ataque, 2: Balón, 3: Borrar
    var isBallVisible by remember { mutableStateOf(false) }
    var ballOffset by remember { mutableStateOf(Offset(140f, 150f)) }

    val attackLines = remember { mutableStateListOf<TacticalLinePath>() }
    val currentPathPoints = remember { mutableStateListOf<Offset>() }

    Column(modifier = Modifier.fillMaxWidth()) {
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

        // CANCHA INTERACTIVA DE VOLEIBOL
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .background(Color(0xFFD97706), shape = RoundedCornerShape(14.dp))
                .border(2.dp, Color.White, shape = RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .padding(10.dp)
                .pointerInput(selectedToolIndex) {
                    // DIBUJAR LÍNEAS CUANDO ESTÁ SELECCIONADA LA HERRAMIENTA "Ataque" (Índice 1)
                    if (selectedToolIndex == 1) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPathPoints.clear()
                                currentPathPoints.add(offset)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentPathPoints.add(change.position)
                            },
                            onDragEnd = {
                                if (currentPathPoints.isNotEmpty()) {
                                    attackLines.add(TacticalLinePath(currentPathPoints.toList()))
                                    currentPathPoints.clear()
                                }
                            }
                        )
                    }
                }
        ) {
            // LÍNEA DE RED
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Color.White)
                    .align(Alignment.TopCenter)
            )

            // CANVAS PARA TRAZAR LÍNEAS DE ATAQUE DIBUJADAS
            Canvas(modifier = Modifier.fillMaxSize()) {
                for (line in attackLines) {
                    val path = Path().apply {
                        line.points.forEachIndexed { index, point ->
                            if (index == 0) moveTo(point.x, point.y)
                            else lineTo(point.x, point.y)
                        }
                    }
                    drawPath(path, color = line.color, style = Stroke(width = 8f, cap = StrokeCap.Round))
                }

                if (currentPathPoints.isNotEmpty()) {
                    val path = Path().apply {
                        currentPathPoints.forEachIndexed { index, point ->
                            if (index == 0) moveTo(point.x, point.y)
                            else lineTo(point.x, point.y)
                        }
                    }
                    drawPath(path, color = Color.Yellow, style = Stroke(width = 8f, cap = StrokeCap.Round))
                }
            }

            // FICHA DE JUGADORES ARRASTRABLES
            TacticalPlayerToken("10", "PUNTA", initialOffset = Offset(20f, 40f))
            TacticalPlayerToken("7", "CENTRAL", initialOffset = Offset(130f, 40f))
            TacticalPlayerToken("4", "COLOC", initialOffset = Offset(240f, 40f))
            TacticalPlayerToken("2", "LÍBERO", initialOffset = Offset(20f, 180f))
            TacticalPlayerToken("9", "RECEP 2", initialOffset = Offset(130f, 220f))
            TacticalPlayerToken("15", "OPUESTO", initialOffset = Offset(240f, 180f))

            // BALÓN MOSTRABLE Y ARRASTRABLE CON EL DEDO
            if (isBallVisible) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(ballOffset.x.roundToInt(), ballOffset.y.roundToInt()) }
                        .size(38.dp)
                        .background(Color(0xFFFFEB3B), shape = CircleShape)
                        .border(2.dp, Color.Black, CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                ballOffset = Offset(
                                    x = (ballOffset.x + dragAmount.x).coerceIn(0f, 800f),
                                    y = (ballOffset.y + dragAmount.y).coerceIn(0f, 1000f)
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏐", fontSize = 22.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // BARRA INFERIOR DE HERRAMIENTAS INTERACTIVAS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg, shape = RoundedCornerShape(12.dp))
                .border(1.dp, CardBorder, shape = RoundedCornerShape(12.dp))
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TacticalToolItem("🖐️ Mover", isSelected = selectedToolIndex == 0) {
                selectedToolIndex = 0
            }
            TacticalToolItem("📈 Ataque", isSelected = selectedToolIndex == 1) {
                selectedToolIndex = 1
            }
            TacticalToolItem("🏐 Balón", isSelected = selectedToolIndex == 2) {
                selectedToolIndex = 2
                isBallVisible = !isBallVisible
            }
            TacticalToolItem("⌫ Borrar", isSelected = false) {
                attackLines.clear()
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = { /* Guardar jugada táctica */ },
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
fun TacticalToolItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) Color(0xFF065F46) else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.clickable { onClick() }
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
fun CoachStatsTab(
    matchesPlayed: Int,
    winRate: Int,
    attackEff: Float,
    serveEff: Float,
    blockEff: Float,
    receptionEff: Float
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("📊  ESTADÍSTICAS DEL EQUIPO", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Resumen analítico en tiempo real", color = TextGray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(title = "Partidos Jugados", value = "$matchesPlayed", modifier = Modifier.weight(1f))
            StatCard(title = "% Victorias", value = "$winRate%", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(title = "Efectividad Remate", value = "${(attackEff * 100).toInt()}%", modifier = Modifier.weight(1f))
            StatCard(title = "Efectividad Saque", value = "${(serveEff * 100).toInt()}%", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("RENDIMIENTO TÁCTICO PONDERADO", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            color = CardBg,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SkillProgressRow(skill = "Ataque", progress = attackEff)
                SkillProgressRow(skill = "Bloqueo", progress = blockEff)
                SkillProgressRow(skill = "Recepción", progress = receptionEff)
                SkillProgressRow(skill = "Saque", progress = serveEff)
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = CardBg,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, color = NeonGreen, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, color = TextGray, fontSize = 12.sp)
        }
    }
}

@Composable
fun SkillProgressRow(skill: String, progress: Float) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = skill, color = TextWhite, fontSize = 13.sp)
            Text(text = "${(progress * 100).toInt()}%", color = TextGray, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = NeonGreen,
            trackColor = Color(0xFF2C2C2C)
        )
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

@Composable
fun MatchDetailDialog(
    match: MatchEvent,
    localClubName: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBg,
        title = {
            Text(
                text = "Resumen del Partido",
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "$localClubName vs ${match.rivalName}",
                    color = NeonGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Fecha: ${match.date}", color = Color.LightGray, fontSize = 13.sp)
                Text(text = "Ubicación: ${match.location}", color = Color.LightGray, fontSize = 13.sp)
                Text(text = "Tipo: ${match.matchType}", color = Color.LightGray, fontSize = 13.sp)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CardBorder)

                Text(
                    text = "Resultado Sets: ${match.homeSets} - ${match.guestSets}",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Puntos Totales: ${match.homePoints} - ${match.guestPoints}",
                    color = TextWhite,
                    fontSize = 14.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CERRAR", color = NeonGreen, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    VolleyVisionTheme {
        HomeScreen(
            clubName = "CV OLÍMPICO",
            matchesList = listOf(
                MatchEvent(
                    rivalName = "DRAKOS",
                    date = "Hoy, 18:30h",
                    location = "Pabellón Norte",
                    matchType = "Liga Oficial",
                    homePoints = 75,
                    guestPoints = 62,
                    homeSets = 3,
                    guestSets = 1,
                    isFinished = true
                )
            ),
            totalMatchesPlayed = 1,
            winPercentage = 100,
            attackEff = 0.72f,
            serveEff = 0.80f,
            blockEff = 0.65f,
            receptionEff = 0.88f
        )
    }
}