// First, let's update the ConfettiProvider to add a new method for multiple sources
package com.metoly.confetti.ui.confetti

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import java.util.UUID
import java.util.Collections

/**
 * Data class representing a request to display confetti.
 *
 * @property id Unique identifier for the request, defaults to a UUID.
 * @property config Configuration for the confetti display, see [ConfettiConfig].
 * @property source Source from which the confetti will originate, see [ConfettiSource].
 * @property customPosition Optional custom position for the confetti source when [source] is [ConfettiSource.CUSTOM].
 */
data class ConfettiRequest(
    val id: String = UUID.randomUUID().toString(),
    val config: ConfettiConfig,
    val source: ConfettiSource,
    val customPosition: Offset? = null
)

/**
 * Represents a source and configuration pair for multi-source confetti.
 */
data class ConfettiSourceConfig(
    val source: ConfettiSource,
    val config: ConfettiConfig = ConfettiConfig(),
    val customPosition: Offset? = null
)

/**
 * Provides and manages confetti requests.
 *
 * This class allows you to present and control confetti animations within your composables.
 * Use [present] to trigger confetti from predefined sources, [presentAtPosition] for confetti
 * from custom positions, [presentMultiSource] for confetti from multiple sources at once,
 * and [stop] or [stopAll] to stop animations. The [activeConfettis] property provides the
 * current list of active confetti requests.
 */
@Stable
class ConfettiProvider {
    private val _internalList = Collections.synchronizedList(mutableListOf<ConfettiRequest>())
    private val _activeConfettis = mutableStateOf<List<ConfettiRequest>>(emptyList())

    val activeConfettis: List<ConfettiRequest> get() = _activeConfettis.value

    fun present(
        config: ConfettiConfig = ConfettiConfig(),
        source: ConfettiSource = ConfettiSource.TOP_CENTER
    ): String {
        val request = ConfettiRequest(
            config = config,
            source = source
        )
        synchronized(_internalList) {
            _internalList.add(request)
            _activeConfettis.value = ArrayList(_internalList)
        }
        return request.id
    }

    fun presentAtPosition(
        position: Offset,
        config: ConfettiConfig = ConfettiConfig()
    ): String {
        val request = ConfettiRequest(
            config = config,
            source = ConfettiSource.CUSTOM,
            customPosition = position
        )
        synchronized(_internalList) {
            _internalList.add(request)
            _activeConfettis.value = ArrayList(_internalList)
        }
        return request.id
    }

    /**
     * Presents confetti from multiple sources at once.
     *
     * @param sourceConfigs List of source and configuration pairs for confetti presentation.
     * @return List of request IDs that can be used to stop individual requests.
     */
    fun presentMultiSource(sourceConfigs: List<ConfettiSourceConfig>): List<String> {
        val requestIds = mutableListOf<String>()

        synchronized(_internalList) {
            sourceConfigs.forEach { sourceConfig ->
                val request = ConfettiRequest(
                    config = sourceConfig.config,
                    source = sourceConfig.source,
                    customPosition = sourceConfig.customPosition
                )
                _internalList.add(request)
                requestIds.add(request.id)
            }
            _activeConfettis.value = ArrayList(_internalList)
        }

        return requestIds
    }

    /**
     * Presents confetti from all available sources with the given configuration.
     *
     * @param config The configuration to use for all confetti bursts.
     * @return List of request IDs that can be used to stop individual requests.
     */
    fun presentAllSources(config: ConfettiConfig = ConfettiConfig()): List<String> {
        val sources = listOf(
            ConfettiSource.TOP_LEFT,
            ConfettiSource.TOP_CENTER,
            ConfettiSource.TOP_RIGHT,
            ConfettiSource.LEFT_CENTER,
            ConfettiSource.CENTER,
            ConfettiSource.RIGHT_CENTER,
            ConfettiSource.BOTTOM_LEFT,
            ConfettiSource.BOTTOM_CENTER,
            ConfettiSource.BOTTOM_RIGHT
        )

        return presentMultiSource(sources.map { ConfettiSourceConfig(it, config) })
    }

    fun stop(id: String) {
        synchronized(_internalList) {
            _internalList.removeAll { it.id == id }
            _activeConfettis.value = ArrayList(_internalList)
        }
    }

    fun stopAll() {
        synchronized(_internalList) {
            _internalList.clear()
            _activeConfettis.value = emptyList()
        }
    }
}

val LocalConfettiProvider = compositionLocalOf<ConfettiProvider> {
    error("ConfettiProvider not found! Make sure to add ConfettiProvider to the composition.")
}

@Composable
fun rememberConfettiProvider(): ConfettiProvider {
    return remember { ConfettiProvider() }
}