package camiloo.com.example.volleyvision

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgMain = Color(0xFF0F172A)
private val CardBg = Color(0xFF1E293B)
private val NeonGreen = Color(0xFF236AF8)

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: (String, String, String, String, String, (Boolean, String?) -> Unit) -> Unit,
    onBackToWelcome: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var club by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Entrenador") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgMain),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "CREAR CUENTA",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Registra tu club y perfil",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            // Nombre
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre Completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = NeonGreen,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Club
            OutlinedTextField(
                value = club,
                onValueChange = { club = it },
                label = { Text("Nombre del Club / Equipo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = NeonGreen,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico (ej: usuario@gmail.com)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = NeonGreen,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (mínimo 6 caracteres)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = NeonGreen,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Selección de Rol
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { selectedRole = "Entrenador" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedRole == "Entrenador") NeonGreen else CardBg
                    )
                ) {
                    Text("Entrenador", color = if (selectedRole == "Entrenador") BgMain else Color.White)
                }

                Button(
                    onClick = { selectedRole = "Jugador" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedRole == "Jugador") NeonGreen else CardBg
                    )
                ) {
                    Text("Jugador", color = if (selectedRole == "Jugador") BgMain else Color.White)
                }
            }

            // Mensaje de Error
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            // Botón de Registrarse
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank() || name.isBlank() || club.isBlank()) {
                        errorMessage = "Por favor completa todos los campos"
                    } else if (!email.contains("@") || !email.contains(".")) {
                        errorMessage = "Ingresa un correo válido (ejemplo@gmail.com)"
                    } else if (password.length < 6) {
                        errorMessage = "La contraseña debe tener al menos 6 caracteres"
                    } else {
                        isLoading = true
                        errorMessage = ""
                        onRegisterSuccess(email.trim(), password.trim(), name.trim(), club.trim(), selectedRole) { success, err ->
                            isLoading = false
                            if (!success) {
                                errorMessage = err ?: "Error al registrar la cuenta en Firebase"
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = BgMain, modifier = Modifier.size(24.dp))
                } else {
                    Text("REGISTRARME", color = BgMain, fontWeight = FontWeight.Bold)
                }
            }

            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onNavigateToLogin = {},
        onRegisterSuccess = { _, _, _, _, _, _ -> },
        onBackToWelcome = {}
    )
}