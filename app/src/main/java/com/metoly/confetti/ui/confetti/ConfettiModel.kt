package com.metoly.confetti.ui.confetti

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * Configuration for the confetti effect.
 *
 * @property count The number of confetti particles to emit. Default is 120.
 * @property fallSpeed The speed at which confetti falls. Default is 5.0f.
 * @property fallAngle The angle at which confetti falls, in degrees. Default is 0f (straight down).
 * @property colors A list of colors to use for the confetti particles. Defaults to a vibrant set of colors.
 * @property sizes A list of sizes to use for the confetti particles. Default sizes are 5f, 10f, 15f, and 20f.
 * @property shapes A list of shapes to use for the confetti particles. Defaults to rectangles, circles, and rounded rectangles.
 */
@Immutable
data class ConfettiConfig(
    val count: Int = 120,
    val fallSpeed: Float = 5.0f,
    val fallAngle: Float = 0f,
    val colors: List<Color> = listOf(
        Color(0xFFEF476F),
        Color(0xFFFFD166),
        Color(0xFF06D6A0),
        Color(0xFF118AB2),
        Color(0xFF073B4C)
    ),
    val sizes: List<Float> = listOf(5f, 10f, 15f, 20f),
    val shapes: List<ConfettiShape> = listOf(
        ConfettiShape.RECT,
        ConfettiShape.CIRCLE,
        ConfettiShape.RECT_ROUNDED
    )
)

enum class ConfettiShape {
    RECT, CIRCLE, RECT_ROUNDED
}

enum class ConfettiSource {
    TOP_LEFT, TOP_CENTER, TOP_RIGHT,
    BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,
    LEFT_CENTER, RIGHT_CENTER, CENTER,
    CUSTOM
}

data class ConfettiParticle(
    val id: Int,
    val color: Color,
    val position: Offset,
    val velocity: Offset,
    val rotation: Float,
    val rotationSpeed: Float,
    val size: Float,
    val alpha: Float = 1f,
    val shape: ConfettiShape,
    val initialY: Float
) {
    companion object {
        fun create(
            id: Int,
            config: ConfettiConfig,
            source: ConfettiSource,
            bounds: Pair<Float, Float>,
            customPosition: Offset? = null
        ): ConfettiParticle {
            val random = Random.Default
            val colors = config.colors
            val sizes = config.sizes
            val shapes = config.shapes
            
            val position = when (source) {
                ConfettiSource.TOP_LEFT -> Offset(
                    x = random.nextFloat() * bounds.first * 0.25f,
                    y = -random.nextFloat() * 50f
                )
                ConfettiSource.TOP_CENTER -> Offset(
                    x = bounds.first * 0.35f + random.nextFloat() * bounds.first * 0.3f,
                    y = -random.nextFloat() * 50f
                )
                ConfettiSource.TOP_RIGHT -> Offset(
                    x = bounds.first * 0.75f + random.nextFloat() * bounds.first * 0.25f,
                    y = -random.nextFloat() * 50f
                )
                ConfettiSource.BOTTOM_LEFT -> Offset(
                    x = random.nextFloat() * bounds.first * 0.25f,
                    y = bounds.second + random.nextFloat() * 50f
                )
                ConfettiSource.BOTTOM_CENTER -> Offset(
                    x = bounds.first * 0.35f + random.nextFloat() * bounds.first * 0.3f,
                    y = bounds.second + random.nextFloat() * 50f
                )
                ConfettiSource.BOTTOM_RIGHT -> Offset(
                    x = bounds.first * 0.75f + random.nextFloat() * bounds.first * 0.25f,
                    y = bounds.second + random.nextFloat() * 50f
                )
                ConfettiSource.LEFT_CENTER -> Offset(
                    x = -random.nextFloat() * 50f,
                    y = bounds.second * 0.3f + random.nextFloat() * bounds.second * 0.4f
                )
                ConfettiSource.RIGHT_CENTER -> Offset(
                    x = bounds.first + random.nextFloat() * 50f,
                    y = bounds.second * 0.3f + random.nextFloat() * bounds.second * 0.4f
                )
                ConfettiSource.CENTER -> Offset(
                    x = bounds.first * 0.4f + random.nextFloat() * bounds.first * 0.2f,
                    y = bounds.second * 0.4f + random.nextFloat() * bounds.second * 0.2f
                )
                ConfettiSource.CUSTOM -> customPosition ?: Offset(
                    x = random.nextFloat() * bounds.first,
                    y = random.nextFloat() * bounds.second
                )
            }
            
            val coneAngle = 70f
            val randomAngleOffset = (random.nextFloat() - 0.5f) * coneAngle
            
            val baseAngle = when (source) {
                ConfettiSource.TOP_LEFT -> 60f
                ConfettiSource.TOP_CENTER -> 90f
                ConfettiSource.TOP_RIGHT -> 120f
                ConfettiSource.BOTTOM_LEFT -> 315f
                ConfettiSource.BOTTOM_CENTER -> 270f
                ConfettiSource.BOTTOM_RIGHT -> 225f
                ConfettiSource.LEFT_CENTER -> 30f
                ConfettiSource.RIGHT_CENTER -> 150f
                ConfettiSource.CENTER -> {
                    val baseDirection = random.nextFloat() * 360f
                    
                    if (baseDirection > 180f && baseDirection < 360f) {
                        200f + (baseDirection % 160f)
                    } else {
                        if (random.nextFloat() < 0.3f) {
                            baseDirection
                        } else {
                            200f + random.nextFloat() * 140f
                        }
                    }
                }
                ConfettiSource.CUSTOM -> random.nextFloat() * 360f
            }
            
            val finalAngle = (baseAngle + randomAngleOffset) * (Math.PI / 180f).toFloat()
            
            val burstFactor = when (source) {
                ConfettiSource.CENTER -> 2.2f
                else -> 3.0f
            }
            val speed = config.fallSpeed * burstFactor * (80f + random.nextFloat() * 60f)
            
            val vx = speed * kotlin.math.cos(finalAngle) 
            val vy = speed * kotlin.math.sin(finalAngle)
            
            val adjustedVy = if (source == ConfettiSource.CENTER && vy < 0) {
                vy * 0.7f
            } else {
                vy
            }
            
            val xVelocityVariation = 50f
            val yVelocityVariation = when (source) {
                ConfettiSource.CENTER -> 10f
                else -> 20f
            }
            
            val extraXVelocity = when (source) {
                ConfettiSource.LEFT_CENTER -> (20f + random.nextFloat() * 30f)
                ConfettiSource.RIGHT_CENTER -> -(20f + random.nextFloat() * 30f)
                ConfettiSource.TOP_LEFT -> (10f + random.nextFloat() * 20f)
                ConfettiSource.TOP_RIGHT -> -(10f + random.nextFloat() * 20f)
                ConfettiSource.CENTER -> {
                    (random.nextFloat() - 0.5f) * 60f
                }
                else -> 0f
            }
            
            val finalVx = vx + (random.nextFloat() - 0.5f) * xVelocityVariation + extraXVelocity
            val finalVy = adjustedVy + (random.nextFloat() - 0.5f) * yVelocityVariation
            
            return ConfettiParticle(
                id = id,
                color = colors[random.nextInt(colors.size)],
                position = position,
                velocity = Offset(finalVx, finalVy),
                rotation = random.nextFloat() * 360f,
                rotationSpeed = (random.nextFloat() - 0.5f) * 40f,
                size = sizes[random.nextInt(sizes.size)],
                shape = shapes[random.nextInt(shapes.size)],
                initialY = position.y
            )
        }
    }

    fun update(deltaTime: Float, config: ConfettiConfig, bounds: Pair<Float, Float>): ConfettiParticle {
        val newX = position.x + velocity.x * deltaTime
        val newY = position.y + velocity.y * deltaTime

        val isCenterExplosion = initialY > bounds.second * 0.3f && initialY < bounds.second * 0.7f &&
                position.x > bounds.first * 0.3f && position.x < bounds.first * 0.7f

        val isBottomSource = initialY > bounds.second * 0.9f

        val gravityForce = when {
            isBottomSource -> 1600.0f
            isCenterExplosion -> 1600.0f
            else -> 150.0f
        }

        val airResistance = 0.98f

        val newVelocity = Offset(
            x = velocity.x * airResistance,
            y = velocity.y + (gravityForce * deltaTime)
        )

        val newAlpha = if (isBottomSource && newVelocity.y > 0 && newY > bounds.second * 0.5f) {

            (bounds.second - newY) / (bounds.second * 0.5f)
        } else {
            1.0f
        }

        return this.copy(
            position = Offset(newX, newY),
            velocity = newVelocity,
            rotation = (rotation + rotationSpeed * deltaTime) % 360f,
            alpha = newAlpha
        )
    }

    fun isOutOfBounds(bounds: Pair<Float, Float>): Boolean {
        return position.x < -size ||
                position.x > bounds.first + size ||
                position.y < -size ||
                position.y > bounds.second + size
    }
} 