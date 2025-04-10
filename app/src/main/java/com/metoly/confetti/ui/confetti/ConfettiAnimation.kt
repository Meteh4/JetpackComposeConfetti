package com.metoly.confetti.ui.confetti

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive

@Composable
fun ConfettiHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val confettiProvider = rememberConfettiProvider()
    
    Box(modifier = modifier) {
        ConfettiCompositionLocalProvider(confettiProvider = confettiProvider) {
            content()
        }
        ConfettiRenderer(confettiProvider = confettiProvider)
    }
}

@Composable
private fun ConfettiCompositionLocalProvider(
    confettiProvider: ConfettiProvider,
    content: @Composable () -> Unit
) {
    androidx.compose.runtime.CompositionLocalProvider(
        LocalConfettiProvider provides confettiProvider
    ) {
        content()
    }
}

/**
 * Data class to keep track of particles for each confetti animation.
 *
 * @property particles The list of [ConfettiParticle] currently active in the animation.
 * @property completed `true` if the animation has finished (all particles generated and disappeared), `false` otherwise.
 * @property totalParticles The total number of particles generated so far for this animation.
 * @property allParticlesGenerated `true` if all particles for the animation have been generated, `false` otherwise.
 */
private data class ConfettiGroup(
    val particles: List<ConfettiParticle> = emptyList(),
    val completed: Boolean = false,
    val totalParticles: Int = 0,
    val allParticlesGenerated: Boolean = false
)

@Composable
private fun ConfettiRenderer(
    confettiProvider: ConfettiProvider,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var lastFrameTime by remember { mutableStateOf(0L) }
    var screenSize by remember { mutableStateOf(Pair(0f, 0f)) }

    val particleGroups = remember { mutableStateMapOf<String, ConfettiGroup>() }

    LaunchedEffect(confettiProvider.activeConfettis) {
        val activeRequests = confettiProvider.activeConfettis.toList()

        val currentIds = particleGroups.keys
        val newRequests = activeRequests.filter { it.id !in currentIds }

        newRequests.forEach { request ->
            particleGroups[request.id] = ConfettiGroup(
                particles = emptyList(),
                completed = false,
                totalParticles = 0,
                allParticlesGenerated = false
            )
        }
        
        // Clean up any particle groups that are no longer in activeConfettis
        val activeIds = activeRequests.map { it.id }.toSet()
        val idsToRemove = particleGroups.keys.filter { it !in activeIds }
        idsToRemove.forEach { particleGroups.remove(it) }
    }
    
    LaunchedEffect(Unit) {
        while (isActive) {
            val frameTime = withInfiniteAnimationFrameMillis { it }
            val deltaTime = if (lastFrameTime == 0L) 0f else (frameTime - lastFrameTime) / 1000f
            lastFrameTime = frameTime

            val activeRequests = confettiProvider.activeConfettis.toList()

            val groupsToUpdate = mutableMapOf<String, ConfettiGroup>()

            activeRequests.forEach { request ->
                val currentGroup = particleGroups[request.id] ?: ConfettiGroup()
                var updatedGroup = currentGroup

                if (!currentGroup.allParticlesGenerated && 
                    currentGroup.totalParticles < request.config.count) {

                    val particlesToAdd = kotlin.math.min(
                        12,
                        request.config.count - currentGroup.totalParticles
                    )
                    
                    val newParticles = List(particlesToAdd) { idx ->
                        ConfettiParticle.create(
                            id = currentGroup.totalParticles + idx,
                            config = request.config,
                            source = request.source,
                            bounds = screenSize,
                            customPosition = request.customPosition
                        )
                    }

                    val newTotalParticles = currentGroup.totalParticles + particlesToAdd
                    val allGenerated = newTotalParticles >= request.config.count
                    
                    updatedGroup = currentGroup.copy(
                        particles = currentGroup.particles + newParticles,
                        totalParticles = newTotalParticles,
                        allParticlesGenerated = allGenerated
                    )
                }

                if (updatedGroup.particles.isNotEmpty()) {
                    val updatedParticles = updatedGroup.particles
                        .map { it.update(deltaTime, request.config, screenSize) }
                        .filter { !it.isOutOfBounds(screenSize) && it.alpha > 0.01f }

                    val isCompleted = updatedGroup.allParticlesGenerated && updatedParticles.isEmpty()
                    
                    updatedGroup = updatedGroup.copy(
                        particles = updatedParticles,
                        completed = isCompleted
                    )

                    if (isCompleted) {
                        groupsToUpdate[request.id] = updatedGroup
                    } else {
                        groupsToUpdate[request.id] = updatedGroup
                    }
                } else {
                    groupsToUpdate[request.id] = updatedGroup
                }
            }

            groupsToUpdate.forEach { (id, group) ->
                particleGroups[id] = group

                if (group.completed) {
                    confettiProvider.stop(id)
                }
            }
        }
    }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
    ) {
        screenSize = Pair(size.width, size.height)

        particleGroups.values.forEach { group ->
            group.particles.forEach { particle ->
                drawParticle(particle)
            }
        }
    }
}

private fun DrawScope.drawParticle(particle: ConfettiParticle) {
    rotate(particle.rotation, pivot = particle.position) {
        when (particle.shape) {
            ConfettiShape.RECT -> {
                drawRect(
                    color = particle.color.copy(alpha = particle.alpha),
                    topLeft = Offset(
                        particle.position.x - particle.size / 2,
                        particle.position.y - particle.size / 2
                    ),
                    size = Size(particle.size, particle.size * 0.7f)
                )
            }
            ConfettiShape.CIRCLE -> {
                drawCircle(
                    color = particle.color.copy(alpha = particle.alpha),
                    radius = particle.size / 2,
                    center = particle.position
                )
            }
            ConfettiShape.RECT_ROUNDED -> {
                drawRoundRect(
                    color = particle.color.copy(alpha = particle.alpha),
                    topLeft = Offset(
                        particle.position.x - particle.size / 2,
                        particle.position.y - particle.size / 2
                    ),
                    size = Size(particle.size, particle.size * 0.7f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(particle.size * 0.2f)
                )
            }
        }
    }
} 