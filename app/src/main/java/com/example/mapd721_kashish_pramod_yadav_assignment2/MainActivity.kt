package com.example.mapd721_kashish_pramod_yadav_assignment2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import com.example.mapd721_kashish_pramod_yadav_assignment2.components.DatePicker
import com.example.mapd721_kashish_pramod_yadav_assignment2.components.HeartRateHistory
import com.example.mapd721_kashish_pramod_yadav_assignment2.components.TimePicker
import com.example.mapd721_kashish_pramod_yadav_assignment2.data.loadHeartRates
import com.example.mapd721_kashish_pramod_yadav_assignment2.data.saveHeartRate
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GradientTheme {
                DisplayHealthData(context = LocalContext.current)
            }
        }
    }
}

@Composable
fun GradientTheme(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A237E), Color(0xFF6A1B9A)) // Dark blue to purple
                )
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun DisplayHealthData(modifier: Modifier = Modifier, context: Context) {
    var heartRateField by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var historyList by remember { mutableStateOf<List<HeartRateRecord>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val client = HealthConnectClient.getOrCreate(context)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title text
            Text(
                "Record Heart Rate",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color(0xFF000080), shape = RoundedCornerShape(6.dp))
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            // Input field
            OutlinedTextField(
                value = heartRateField,
                onValueChange = { heartRateField = it },
                label = {
                    Text(
                        "Enter Heart Rate (BPM)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                textStyle = TextStyle(color = Color.White)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date and time pickers
            DatePicker(selectedDate = selectedDate, onDateChange = { selectedDate = it })
            TimePicker(selectedTime = selectedTime, onTimeChange = { selectedTime = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            historyList = loadHeartRates(client)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().border(2.dp, Color.White, shape = RoundedCornerShape(12.dp)), // White border,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White, // White texts
                        containerColor = Color.Transparent // Removing background
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Load")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val bpm = heartRateField.toIntOrNull()
                        if (bpm != null && bpm in 1..300) {
                            coroutineScope.launch {
                                try {
                                    saveHeartRate(client, bpm, selectedDate, selectedTime)
                                    Toast.makeText(
                                        context,
                                        "Heart rate saved: $bpm BPM",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } catch (e: Exception) {
                                    Log.e("HealthConnect", "Failed to save heart rate", e)
                                    Toast.makeText(
                                        context,
                                        "Failed to save heart rate",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Enter a valid heart rate (1-300 BPM)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    // White border here
                    modifier = Modifier.fillMaxWidth().border(2.dp, Color.White, shape = RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Color(0xFF008080)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save")
                }
            }

            // Heart Rate History Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Heart Rate History",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                HeartRateHistory(itemsList = historyList)
            }

            // Student Info Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color.White, shape = RoundedCornerShape(12.dp)) // White border,
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Name: Kashish Pramod Yadav",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Student ID: 301485842",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewDisplayHealthData() {
    GradientTheme {
        DisplayHealthData(context = LocalContext.current)
    }
}
