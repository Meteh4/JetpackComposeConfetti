package com.metoly.confetti.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.metoly.confetti.ui.confetti.ConfettiConfig
import com.metoly.confetti.ui.confetti.ConfettiHost
import com.metoly.confetti.ui.confetti.ConfettiSource
import com.metoly.confetti.ui.confetti.ConfettiSourceConfig
import com.metoly.confetti.ui.confetti.LocalConfettiProvider
import com.metoly.confetti.ui.theme.ConfettiTheme

@Composable
fun ExampleScreen() {
    val localConfettiProvider = LocalConfettiProvider.current

    val customConfig = remember {
        ConfettiConfig(
            count = 150,
            fallSpeed = 6.0f,
            colors = listOf(
                Color(0xFFFF5252),
                Color(0xFFFFEB3B),
                Color(0xFF66BB6A),
                Color(0xFF42A5F5),
                Color(0xFFAB47BC)
            ),
            sizes = listOf(8f, 12f, 16f)
        )
    }

    val sources = remember {
        listOf(
            ConfettiSource.TOP_LEFT to "Top Left",
            ConfettiSource.TOP_CENTER to "Top Center",
            ConfettiSource.TOP_RIGHT to "Top Right",
            ConfettiSource.LEFT_CENTER to "Left Center",
            ConfettiSource.CENTER to "Center",
            ConfettiSource.RIGHT_CENTER to "Right Center",
            ConfettiSource.BOTTOM_LEFT to "Bottom Left",
            ConfettiSource.BOTTOM_CENTER to "Bottom Center",
            ConfettiSource.BOTTOM_RIGHT to "Bottom Right"
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Confetti Demo",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        localConfettiProvider.presentAllSources(customConfig)
                    }
                ) {
                    Text("ALL SOURCES!")
                }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        localConfettiProvider.stopAll()
                    }
                ) {
                    Text("Stop All")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val sourceConfigs = listOf(
                        ConfettiSourceConfig(ConfettiSource.TOP_LEFT, customConfig),
                        ConfettiSourceConfig(ConfettiSource.TOP_RIGHT, customConfig),
                        ConfettiSourceConfig(ConfettiSource.BOTTOM_LEFT, customConfig.copy(
                            colors = listOf(Color.Yellow, Color.Green, Color.Cyan)
                        )),
                        ConfettiSourceConfig(ConfettiSource.BOTTOM_RIGHT, customConfig.copy(
                            colors = listOf(Color.Magenta, Color.Red, Color.Blue)
                        ))
                    )

                    localConfettiProvider.presentMultiSource(sourceConfigs)
                }
            ) {
                Text("Corner Celebration")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sources) { (source, name) ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    localConfettiProvider.present(
                                        config = customConfig.copy(
                                            count = 80
                                        ),
                                        source = source
                                    )
                                }
                            ) {
                                Text("Test")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExampleScreenPreview() {
    ConfettiTheme {
        ConfettiHost {
            ExampleScreen()
        }
    }
}