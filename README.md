# 🎊 Confetti Animation Component

<p align="center">
  <img src="screenshots/confetti_demo.gif" alt="Confetti Animation Demo" width="300"/>
</p>

<p align="center">
  <a href="https://opensource.org/licenses/MIT"><img alt="License" src="https://img.shields.io/badge/License-MIT-blue.svg"></a>
  <a href="#"><img alt="API" src="https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat"></a>
  <a href="#"><img alt="Platform" src="https://img.shields.io/badge/platform-Android-green"></a>
  <a href="#"><img alt="Compose" src="https://img.shields.io/badge/Jetpack%20Compose-Latest-purple"></a>
</p>

## 📖 About the Project

**Confetti** is a modern particle animation project developed with Jetpack Compose for Android applications. It offers a flexible API to create beautiful confetti animations from different screen positions - perfect for celebrations, achievements, or adding playful interactions to your app.

## ✨ Features

- **🎨 Fully Customizable**  
  Customize colors, shapes, sizes, particle count, and physics properties to create unique confetti effects.

- **🚀 High Performance**  
  Physics-based animation system with efficient particle rendering for smooth animations.

- **📍 Multiple Source Points**  
  Launch confetti from any screen position: top, bottom, center, corners, or custom positions.

- **🔄 Multiple Animations**  
  Run several confetti animations simultaneously with different configurations.

- **📱 Simple Integration**  
  Clean API design with Jetpack Compose for easy integration.

## 📱 Demo

<p align="center">
  <img src="screenshots/multiple_sources.gif" width="200" alt="Multiple Sources"/>
  <img src="screenshots/center_burst.gif" width="200" alt="Center Burst"/>
  <img src="screenshots/corner_celebration.gif" width="200" alt="Corner Celebration"/>
</p>

## 🚀 Usage

### Basic Implementation

1. **Wrap your content with ConfettiHost**

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ConfettiHost(modifier = Modifier.fillMaxSize()) {
                    // Your app content here
                    AppScreen()
                }
            }
        }
    }
}
```

2. **Trigger confetti when needed**

```kotlin
@Composable
fun AppScreen() {
    val confettiProvider = LocalConfettiProvider.current
    
    Button(onClick = {
        confettiProvider.present(
            config = ConfettiConfig(
                count = 150,                // Number of particles
                fallSpeed = 5.0f,           // Base fall speed
                colors = listOf(            // Custom colors
                    Color(0xFFEF476F),
                    Color(0xFFFFD166),
                    Color(0xFF06D6A0)
                ),
                shapes = listOf(            // Particle shapes
                    ConfettiShape.RECT,
                    ConfettiShape.CIRCLE,
                    ConfettiShape.RECT_ROUNDED
                )
            ),
            source = ConfettiSource.TOP_CENTER  // Launch position
        )
    }) {
        Text("Celebrate!")
    }
}
```

## 🎨 Customization

### ConfettiConfig Parameters

| Parameter | Description | Default |
|-----------|-------------|---------|
| `count` | Number of confetti particles | 120 |
| `fallSpeed` | Base speed multiplier | 5.0f |
| `fallAngle` | Angle of fall in degrees | 0f |
| `colors` | List of colors for particles | 5 vibrant colors |
| `sizes` | List of particle sizes | [5f, 10f, 15f, 20f] |
| `shapes` | Types of shapes to use | [RECT, CIRCLE, RECT_ROUNDED] |

### Launch Positions

Launch confetti from any of these predefined positions:

```
TOP_LEFT      TOP_CENTER      TOP_RIGHT
   ↘️             ⬇️              ↙️
LEFT_CENTER    CENTER     RIGHT_CENTER
   ➡️             💥             ⬅️
BOTTOM_LEFT  BOTTOM_CENTER  BOTTOM_RIGHT
   ↗️             ⬆️              ↖️
```

Or use a custom position:

```kotlin
confettiProvider.presentAtPosition(
    position = Offset(x = 100f, y = 100f),
    config = yourConfig
)
```

### Multiple Sources Example

Create celebrations from multiple points simultaneously:

```kotlin
val sourceConfigs = listOf(
    ConfettiSourceConfig(ConfettiSource.TOP_LEFT, yourConfig),
    ConfettiSourceConfig(ConfettiSource.TOP_RIGHT, yourConfig),
    ConfettiSourceConfig(ConfettiSource.BOTTOM_LEFT, 
        yourConfig.copy(colors = listOf(Color.Yellow, Color.Green, Color.Cyan))
    ),
    ConfettiSourceConfig(ConfettiSource.BOTTOM_RIGHT, 
        yourConfig.copy(colors = listOf(Color.Magenta, Color.Red, Color.Blue))
    )
)

confettiProvider.presentMultiSource(sourceConfigs)
```

## 🏗️ Advanced Usage

### Animation Control

```kotlin
// Launch and store ID to stop specific animation later
val confettiId = confettiProvider.present(...)
confettiProvider.stop(confettiId)

// Stop all active animations
confettiProvider.stopAll()

// Launch from all sources at once
confettiProvider.presentAllSources(yourConfig)
```

## 🛠️ Technical Details

### Architecture Components

This project consists of several key components:

1. **ConfettiHost**: The main container component that wraps your content and renders the confetti animations.

2. **ConfettiProvider**: Core manager that handles confetti requests and lifecycle.

3. **ConfettiParticle**: Data class representing each individual confetti particle with properties like:
   - Position and velocity
   - Color, shape, and size
   - Rotation and alpha values

4. **ConfettiRenderer**: Internal component that performs the physics calculation and rendering of particles.

### Particle Physics

Each particle follows realistic physical motion:

- **Gravity**: Pulls particles downward with configurable force
- **Air Resistance**: Gradually slows horizontal movement
- **Rotation**: Particles spin as they fall
- **Source-Based Behavior**: Different launch patterns based on source location
- **Alpha Fading**: Particles fade out based on travel distance

### Optimizations

- Particles outside the visible bounds are automatically removed
- Batch particle creation to distribute processing load
- Efficient Canvas rendering with rotation transformations
- Synchronized collection management for thread safety

## 📊 Performance Tips

For optimal performance:

1. **Particle Count**: Adjust based on device capabilities (100-200 recommended)
2. **Multiple Animations**: Be mindful when running many simultaneous effects
3. **Shape Selection**: Circle shapes are slightly more efficient to render
4. **Clean Up**: Call `stopAll()` when transitioning between screens
