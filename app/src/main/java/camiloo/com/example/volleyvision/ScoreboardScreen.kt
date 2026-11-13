package camiloo.com.example.volleyvision

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import camiloo.com.example.volleyvision.ui.theme.*

data class PointHistory(
    val isHome: Boolean,
    val actionType: String
)

@Composable
fun ScoreboardScreen(
    localTeamName: String = "CV OLÍMPICO",
    rivalName: String = "VISITANTE",
    courtPositions: Map<Int, PlayerMember?> = emptyMap(),
    onNavigateToTactics: () -> Unit = {},
    onPointScoredByAction: (String) -> Unit = {},
    onGuestPointScored: () -> Unit = {},
    onUndoPointByAction: (String) -> Unit = {},
    onFinishMatch: (
        homePoints: Int, guestPoints: Int, homeSets: Int, guestSets: Int,
        attackPts: Int, blockPts: Int, servePts: Int
    ) -> Unit = { _, _, _, _, _, _, _ -> }
) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    var homePoints by remember { mutableIntStateOf(0) }
    var guestPoints by remember { mutableIntStateOf(0) }
    var homeSets by remember { mutableIntStateOf(0) }
    var guestSets by remember { mutableIntStateOf(0) }

    var homeAttackPoints by remember { mutableIntStateOf(0) }
    var homeBlockPoints by remember { mutableIntStateOf(0) }
    var homeServePoints by remember { mutableIntStateOf(0) }

    val historyStack = remember { mutableStateListOf<PointHistory>() }

    // Jugadores asignados por zona táctica (Zona 4: Remate, Zona 3: Bloqueo, Zona 1: Saque)
    val fallbackPlayer = courtPositions.values.filterNotNull().firstOrNull()
    val attackPlayer = courtPositions[4] ?: fallbackPlayer
    val blockPlayer = courtPositions[3] ?: fallbackPlayer
    val servePlayer = courtPositions[1] ?: fallbackPlayer

    fun checkSetWinner(newHomePts: Int, newGuestPts: Int) {
        if (newHomePts >= 25 && (newHomePts - newGuestPts) >= 2) {
            homeSets++
            homePoints = 0
            guestPoints = 0
            historyStack.clear()
        } else if (newGuestPts >= 25 && (newGuestPts - newHomePts) >= 2) {
            guestSets++
            homePoints = 0
            guestPoints = 0
            historyStack.clear()
        }
    }

    fun addPoint(isHome: Boolean, action: String) {
        if (isHome) {
            homePoints++
            when (action) {
                "REMATE" -> homeAttackPoints++
                "BLOQUEO" -> homeBlockPoints++
                "SAQUE" -> homeServePoints++
            }
            onPointScoredByAction(action)
        } else {
            guestPoints++
            onGuestPointScored()
        }
        historyStack.add(PointHistory(isHome = isHome, actionType = action))
        checkSetWinner(homePoints, guestPoints)
    }

    fun undoLastPoint() {
        if (historyStack.isNotEmpty()) {
            val lastIndex = historyStack.size - 1
            val lastPoint = historyStack.removeAt(lastIndex)

            if (lastPoint.isHome) {
                if (homePoints > 0) homePoints--
                when (lastPoint.actionType) {
                    "REMATE" -> if (homeAttackPoints > 0) homeAttackPoints--
                    "BLOQUEO" -> if (homeBlockPoints > 0) homeBlockPoints--
                    "SAQUE" -> if (homeServePoints > 0) homeServePoints--
                }
                onUndoPointByAction(lastPoint.actionType)
            } else {
                if (guestPoints > 0) guestPoints--
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141414))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // ENCABEZADO SUPERIOR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = localTeamName.uppercase(),
                color = Color(0xFF00E5FF),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = rivalName.uppercase(),
                color = Color(0xFFFF5252),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // PANEL DE MARCADORES PRINCIPALES Y ZONAS DE PUNTOS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // RECUADRO AZUL - EQUIPO LOCAL
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF0288D1), shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // SUBZONA REMATE
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { addPoint(isHome = true, action = "REMATE") }
                            .padding(6.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = attackPlayer?.let { "#${it.dorsal} ${it.name.substringBefore(" ")}" } ?: "Sin jugador",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("💥 REMATE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    // SUBZONA BLOQUEO
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { addPoint(isHome = true, action = "BLOQUEO") }
                            .padding(6.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = blockPlayer?.let { "#${it.dorsal} ${it.name.substringBefore(" ")}" } ?: "Sin jugador",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("🛡️ BLOQUEO", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    // SUBZONA SAQUE
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { addPoint(isHome = true, action = "SAQUE") }
                            .padding(6.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = servePlayer?.let { "#${it.dorsal} ${it.name.substringBefore(" ")}" } ?: "Sin jugador",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("🚀 SAQUE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = String.format("%02d", homePoints),
                    color = Color.White,
                    fontSize = 90.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            // CONTROLES CENTRALES
            Column(
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF0288D1),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("$homeSets", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text("-", color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    Surface(
                        color = Color(0xFFFF5252),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("$guestSets", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                IconButton(onClick = { undoLastPoint() }) {
                    Text("↺", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                IconButton(onClick = {
                    val tempP = homePoints; homePoints = guestPoints; guestPoints = tempP
                    val tempS = homeSets; homeSets = guestSets; guestSets = tempS
                }) {
                    Text("🔄", fontSize = 18.sp)
                }

                IconButton(onClick = onNavigateToTactics) {
                    Text("📊", fontSize = 18.sp)
                }
            }

            // RECUADRO ROJO - EQUIPO VISITANTE
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFFFF5252), shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { addPoint(isHome = false, action = "REMATE") }
                            .padding(6.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text("💥 REMATE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { addPoint(isHome = false, action = "BLOQUEO") }
                            .padding(6.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text("🛡️ BLOQUEO", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { addPoint(isHome = false, action = "SAQUE") }
                            .padding(6.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text("🚀 SAQUE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = String.format("%02d", guestPoints),
                    color = Color.White,
                    fontSize = 90.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // BOTÓN INFERIOR DE FINALIZACIÓN
        Button(
            onClick = {
                onFinishMatch(
                    homePoints, guestPoints, homeSets, guestSets,
                    homeAttackPoints, homeBlockPoints, homeServePoints
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("FINALIZAR PARTIDO", color = BgMain, fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
fun ScoreboardScreenPreview() {
    VolleyVisionTheme {
        ScoreboardScreen()
    }
}
