package com.metoly.confetti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.metoly.confetti.ui.confetti.ConfettiConfig
import com.metoly.confetti.ui.confetti.ConfettiHost
import com.metoly.confetti.ui.confetti.ConfettiSource
import com.metoly.confetti.ui.confetti.LocalConfettiProvider
import com.metoly.confetti.ui.screens.ExampleScreen
import com.metoly.confetti.ui.theme.ConfettiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConfettiTheme {
                ConfettiHost(modifier = Modifier.fillMaxSize()) {
                    ExampleScreen()
                }
            }
        }
    }
}