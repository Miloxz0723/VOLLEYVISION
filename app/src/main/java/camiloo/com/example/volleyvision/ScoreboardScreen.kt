package camiloo.com.example.volleyvision

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import camiloo.com.example.volleyvision.ui.theme.*

@Composable
fun ScoreboardScreen(
    localTeamName: String = "CV OLÍMPICO",
    rivalName: String = "VISITANTE",
    onNavigateToTactics: () -> Unit = {},
    onFinishMatch: () -> Unit = {}
) {
    var homePoints by remember { mutableIntStateOf(0) }
    var guestPoints by remember { mutableIntStateOf(0) }
    var homeSets by remember { mutableIntStateOf(0) }
    var guestSets by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. ENCABEZADO SIMPLE (Nombres de equipos)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = localTeamName.uppercase(),
                color = Color(0xFF00E5FF),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = rivalName.uppercase(),
                color = Color(0xFFFF5252),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // 2. BLOQUE CENTRAL (Tablero Principal)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Puntos Local (Tocar para sumar)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF0288D1), shape = RoundedCornerShape(16.dp))
                    .clickable { homePoints++ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%02d", homePoints),
                    color = Color.White,
                    fontSize = 100.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Marcador Central de Sets y Acciones
            Column(
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Sets (Local vs Visitante)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF0288D1),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("$homeSets", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text("-", color = Color.Gray, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    Surface(
                        color = Color(0xFFFF5252),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("$guestSets", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Controles simples (Deshacer, Cambiar Lado, Pizarra)
                IconButton(onClick = {
                    if (homePoints > 0) homePoints--
                    if (guestPoints > 0) guestPoints--
                }) {
                    Text("↺", fontSize = 22.sp, color = Color.White)
                }

                IconButton(onClick = {
                    val tempP = homePoints; homePoints = guestPoints; guestPoints = tempP
                    val tempS = homeSets; homeSets = guestSets; guestSets = tempS
                }) {
                    Text("🔄", fontSize = 20.sp)
                }

                IconButton(onClick = onNavigateToTactics) {
                    Text("📊", fontSize = 20.sp)
                }
            }

            // Puntos Visitante (Tocar para sumar)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFFFF5252), shape = RoundedCornerShape(16.dp))
                    .clickable { guestPoints++ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%02d", guestPoints),
                    color = Color.White,
                    fontSize = 100.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // 3. BOTÓN INFERIOR
        Button(
            onClick = onFinishMatch,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("FINALIZAR PARTIDO", color = BgMain, fontWeight = FontWeight.Black, fontSize = 14.sp)
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
fun ScoreboardScreenSimplePreview() {
    VolleyVisionTheme {
        ScoreboardScreen(localTeamName = "CV OLÍMPICO", rivalName = "DRAKOS")
    }
}

