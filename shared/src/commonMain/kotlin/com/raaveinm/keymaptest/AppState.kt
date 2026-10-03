package com.raaveinm.keymaptest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import com.raaveinm.keymaptest.keybinding.AppAction
import com.raaveinm.keymaptest.keybinding.KeyChord
import com.raaveinm.keymaptest.keybinding.KeyMap
import com.raaveinm.keymaptest.keybinding.defaultBindings

private const val MAX_LOG_SIZE: Int = 60

///////////////////////////////////////////////
/// Model
///////////////////////////////////////////////

/** One card in the "Caught keys" list. Repeats of the same chord are folded into [count]. */
data class CaughtKey(
    val id: Long, // stable key for the lazy grid
    val chord: KeyChord,
    val action: AppAction?, // `null` -> the chord was caught but nothing is bound to it
    val count: Int = 1,
)

/** One-shot feedback after a rebinding operation, rendered by the status card. */
sealed interface Notice {
    data class Rebound(val action: AppAction, val chord: KeyChord, val displaced: AppAction?) : Notice
    data class Cleared(val action: AppAction) : Notice
    data object Cancelled : Notice
    data object ResetAll : Notice
}

///////////////////////////////////////////////
/// State
///////////////////////////////////////////////

/**
 * Everything the screen needs. The only entry point for input is [onKeyEvent]: hook it to the window
 * (`Window(onPreviewKeyEvent = state::onKeyEvent)`) so keys are caught no matter what has focus.
 */
class AppState(val platform: Platform = getPlatform()) {

    val keyMap: KeyMap = KeyMap(defaultBindings(platform.isMac))

    /** Newest first. */
    val caughtKeys: SnapshotStateList<CaughtKey> = mutableStateListOf()

    val triggerCounts: SnapshotStateMap<AppAction, Int> = mutableStateMapOf()

    /** Non-null while the user is choosing a new shortcut for this action. */
    var recordingFor: AppAction? by mutableStateOf(null)
        private set

    var notice: Notice? by mutableStateOf(null)
        private set

    var darkTheme: Boolean by mutableStateOf(false)
        private set

    private var nextId: Long = 0L

    ///////////////////////////////////////////////
    /// Input
    ///////////////////////////////////////////////

    /** Returns `true` when the event was consumed (a bound shortcut fired, or we are recording). */
    fun onKeyEvent(event: KeyEvent): Boolean {
        val recording: AppAction? = recordingFor
        if (event.type != KeyEventType.KeyDown) return recording != null // swallow key-ups too, or Space/Enter would "click" the focused button

        val chord: KeyChord? = KeyChord.from(event)
        if (recording != null) {
            if (chord != null) finishRecording(recording, chord, event.key)
            return true
        }

        if (chord == null) return false // modifier pressed on its own
        val action: AppAction? = keyMap.resolve(chord)
        log(chord, action)
        if (action != null) execute(action)
        return action != null
    }

    ///////////////////////////////////////////////
    /// Rebinding
    ///////////////////////////////////////////////

    fun startRecording(action: AppAction) {
        recordingFor = action
        notice = null
    }

    fun cancelRecording() {
        recordingFor = null
        notice = Notice.Cancelled
    }

    fun clearBinding(action: AppAction) {
        keyMap.unbind(action)
        recordingFor = null
        notice = Notice.Cleared(action)
    }

    fun resetAll() {
        keyMap.resetAll()
        recordingFor = null
        notice = Notice.ResetAll
    }

    private fun finishRecording(action: AppAction, chord: KeyChord, pressed: Key) {
        // Plain Escape is reserved for aborting, so it can't be bound
        if (pressed == Key.Escape && chord == KeyChord(Key.Escape)) {
            cancelRecording()
            return
        }
        val displaced: AppAction? = keyMap.rebind(action, chord)
        recordingFor = null
        notice = Notice.Rebound(action, chord, displaced)
    }

    ///////////////////////////////////////////////
    /// Actions
    ///////////////////////////////////////////////

    //TODO(replace the dummy actions with real behaviour)
    private fun execute(action: AppAction) {
        triggerCounts[action] = (triggerCounts[action] ?: 0) + 1
        when (action) {
            AppAction.ToggleTheme -> darkTheme = !darkTheme
            else -> Unit // dummy: the trigger counter + highlighted card is the whole effect
        }
    }

    private fun log(chord: KeyChord, action: AppAction?) {
        val latest: CaughtKey? = caughtKeys.firstOrNull()
        if (latest != null && latest.chord == chord) {
            caughtKeys[0] = latest.copy(count = latest.count + 1) // held key / repeated press -> one card with a counter
            return
        }
        caughtKeys.add(0, CaughtKey(id = nextId++, chord = chord, action = action))
        if (caughtKeys.size > MAX_LOG_SIZE) caughtKeys.removeAt(caughtKeys.lastIndex)
    }
}
