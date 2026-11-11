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

// Modelo auxiliar para registrar la última acción y deshacer con precisión
data class PointHistory(
    val isHome: Boolean,
    val actionType: String // "REMATE", "BLOQUEO", "SAQUE"
)

@Composable
fun ScoreboardScreen(
    localTeamName: String = "CV OLÍMPICO",
    rivalName: String = "VISITANTE",
    onNavigateToTactics: () -> Unit = {},
    onPointScoredByAction: (String) -> Unit = {}, // Notifica al ViewModel qué acción suma punto
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

    // Registros detallados por acción técnica del equipo local
    var homeAttackPoints by remember { mutableIntStateOf(0) }
    var homeBlockPoints by remember { mutableIntStateOf(0) }
    var homeServePoints by remember { mutableIntStateOf(0) }

    // Historial para el botón deshacer (círculo amarillo)
    val historyStack = remember { mutableStateListOf<PointHistory>() }

    // Verificación de ganador de set a los 25 puntos (con ventaja de 2)
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

    // Registra la suma de un punto según la subzona presionada
    fun addPoint(isHome: Boolean, action: String) {
        if (isHome) {
            homePoints++
            when (action) {
                "REMATE" -> homeAttackPoints++
                "BLOQUEO" -> homeBlockPoints++
                "SAQUE" -> homeServePoints++
            }
            // Dispara la asignación automática al jugador posicionado
            onPointScoredByAction(action)
        } else {
            guestPoints++
        }
        historyStack.add(PointHistory(isHome = isHome, actionType = action))
        checkSetWinner(homePoints, guestPoints)
    }

    // Botón deshacer (círculo amarillo): Resta únicamente a la última anotación registrada
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
            // RECUADRO AZUL - EQUIPO LOCAL (DIVIDIDO EN 3 SUBZONAS)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF0288D1), shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { addPoint(isHome = true, action = "REMATE") }
                            .padding(6.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text("💥 REMATE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { addPoint(isHome = true, action = "BLOQUEO") }
                            .padding(6.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text("🛡️ BLOQUEO", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { addPoint(isHome = true, action = "SAQUE") }
                            .padding(6.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
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
                // MINI MARCADOR DE SETS (CÍRCULO MORADO)
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

                // BOTÓN DESHACER PUNTO INDIVIDUAL (CÍRCULO AMARILLO)
                IconButton(onClick = { undoLastPoint() }) {
                    Text("↺", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                // CAMBIAR LADOS EN PANTALLA
                IconButton(onClick = {
                    val tempP = homePoints; homePoints = guestPoints; guestPoints = tempP
                    val tempS = homeSets; homeSets = guestSets; guestSets = tempS
                }) {
                    Text("🔄", fontSize = 18.sp)
                }

                // IR A LA PIZARRA TÁCTICA 2D
                IconButton(onClick = onNavigateToTactics) {
                    Text("📊", fontSize = 18.sp)
                }
            }

            // RECUADRO ROJO - EQUIPO VISITANTE (DIVIDIDO EN 3 SUBZONAS)
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
