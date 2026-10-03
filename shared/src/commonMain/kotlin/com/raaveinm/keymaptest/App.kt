package com.raaveinm.keymaptest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.raaveinm.keymaptest.keybinding.AppAction
import com.raaveinm.keymaptest.keybinding.KeyChord
import keymaptest.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Window title; exposed because the generated `Res` class is internal to this module. */
@Composable
fun appTitle(): String = stringResource(Res.string.app_title)

///////////////////////////////////////////////
/// Screen
///////////////////////////////////////////////

@Composable
fun App(state: AppState) {
    MaterialTheme(colorScheme = if (state.darkTheme) darkColorScheme() else lightColorScheme()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(Res.string.app_title),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = stringResource(Res.string.app_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                StatusCard(state = state)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    BindingsPanel(
                        modifier = Modifier.weight(1f),
                        state = state,
                    )
                    CaughtKeysPanel(
                        modifier = Modifier.weight(1f),
                        state = state,
                    )
                }
            }
        }
    }
}

///////////////////////////////////////////////
/// Status
///////////////////////////////////////////////

@Composable
private fun StatusCard(state: AppState) {
    val recording: AppAction? = state.recordingFor
    val message: String = when {
        recording != null -> stringResource(Res.string.status_recording, stringResource(recording.title))
        else -> when (val notice: Notice? = state.notice) {
            is Notice.Rebound -> {
                val chord: String = notice.chord.label(state.platform.isMac)
                val displaced: AppAction? = notice.displaced
                if (displaced == null) {
                    stringResource(Res.string.status_rebound, stringResource(notice.action.title), chord)
                } else {
                    stringResource(
                        Res.string.status_rebound_displaced,
                        stringResource(notice.action.title),
                        chord,
                        stringResource(displaced.title),
                    )
                }
            }
            is Notice.Cleared -> stringResource(Res.string.status_cleared, stringResource(notice.action.title))
            Notice.Cancelled -> stringResource(Res.string.status_cancelled)
            Notice.ResetAll -> stringResource(Res.string.status_reset)
            null -> stringResource(Res.string.status_hint)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (recording != null) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            },
        ),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            text = message,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

///////////////////////////////////////////////
/// Bindings (left)
///////////////////////////////////////////////

@Composable
private fun BindingsPanel(
    modifier: Modifier = Modifier,
    state: AppState,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.bindings_title),
                style = MaterialTheme.typography.titleLarge,
            )
            TextButton(onClick = state::resetAll) {
                Text(stringResource(Res.string.reset_all))
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(AppAction.entries) { action: AppAction ->
                BindingCard(
                    state = state,
                    action = action,
                )
            }
        }
    }
}

@Composable
private fun BindingCard(
    state: AppState,
    action: AppAction,
) {
    val chord: KeyChord? = state.keyMap.chordFor(action)
    val isRecording: Boolean = state.recordingFor == action
    val justTriggered: Boolean = state.caughtKeys.firstOrNull()?.action == action
    val triggered: Int = state.triggerCounts[action] ?: 0

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (justTriggered) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            },
        ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(action.title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(action.description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (triggered > 0) {
                    Text(
                        text = stringResource(Res.string.triggered_count, triggered),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            ChordBadge(
                chord = chord,
                isMac = state.platform.isMac,
            )
            Column(horizontalAlignment = Alignment.End) {
                if (isRecording) {
                    FilledTonalButton(onClick = state::cancelRecording) {
                        Text(stringResource(Res.string.cancel))
                    }
                } else {
                    OutlinedButton(onClick = { state.startRecording(action) }) {
                        Text(stringResource(Res.string.rebind))
                    }
                }
                if (chord != null && !isRecording) {
                    TextButton(onClick = { state.clearBinding(action) }) {
                        Text(stringResource(Res.string.clear))
                    }
                }
            }
        }
    }
}

/** Monospaced "keycap" for a chord, or a muted placeholder when the action is unbound. */
@Composable
private fun ChordBadge(
    chord: KeyChord?,
    isMac: Boolean,
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (chord != null) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            Color.Transparent
        },
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            text = chord?.label(isMac) ?: stringResource(Res.string.unassigned),
            style = MaterialTheme.typography.labelLarge,
            fontFamily = FontFamily.Monospace,
            color = if (chord != null) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.outline
            },
        )
    }
}

///////////////////////////////////////////////
/// Caught keys (right)
///////////////////////////////////////////////

@Composable
private fun CaughtKeysPanel(
    modifier: Modifier = Modifier,
    state: AppState,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.caught_title),
            style = MaterialTheme.typography.titleLarge,
        )
        if (state.caughtKeys.isEmpty()) {
            Text(
                text = stringResource(Res.string.caught_empty),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 180.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = state.caughtKeys,
                key = { it.id },
            ) { caught: CaughtKey ->
                CaughtKeyCard(
                    caught = caught,
                    isMac = state.platform.isMac,
                )
            }
        }
    }
}

@Composable
private fun CaughtKeyCard(
    caught: CaughtKey,
    isMac: Boolean,
) {
    val action: AppAction? = caught.action
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (action != null) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = caught.chord.label(isMac),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                )
                if (caught.count > 1) {
                    Text(
                        text = stringResource(Res.string.caught_count, caught.count),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            Text(
                text = if (action != null) {
                    stringResource(Res.string.action_arrow, stringResource(action.title))
                } else {
                    stringResource(Res.string.no_action)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (action != null) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}
