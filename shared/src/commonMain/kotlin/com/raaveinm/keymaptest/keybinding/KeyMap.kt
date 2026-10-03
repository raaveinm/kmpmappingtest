package com.raaveinm.keymaptest.keybinding

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap

/**
 * The single source of truth for "which chord triggers which action".
 *
 * Backed by a snapshot state map, so any composable that reads [chordFor] / [resolve]
 * recomposes automatically after a rebind. A chord maps to at most one action; an action may be unbound.
 */
class KeyMap(private val defaults: Map<AppAction, KeyChord>) {

    private val bindings: SnapshotStateMap<AppAction, KeyChord?> = mutableStateMapOf()

    init {
        resetAll()
    }

    ///////////////////////////////////////////////
    /// Queries
    ///////////////////////////////////////////////

    fun chordFor(action: AppAction): KeyChord? = bindings[action]

    /** The action currently bound to [chord], if any. */
    fun resolve(chord: KeyChord): AppAction? = AppAction.entries.firstOrNull { bindings[it] == chord }

    ///////////////////////////////////////////////
    /// Mutations
    ///////////////////////////////////////////////

    /**
     * Binds [chord] to [action]. If another action already owned the chord it is unbound
     * (no silent double-binding) and returned so the UI can tell the user.
     */
    fun rebind(action: AppAction, chord: KeyChord): AppAction? {
        val displaced: AppAction? = resolve(chord)?.takeIf { it != action }
        if (displaced != null) bindings[displaced] = null
        bindings[action] = chord
        return displaced
    }

    fun unbind(action: AppAction) {
        bindings[action] = null
    }

    fun resetAll() {
        AppAction.entries.forEach { bindings[it] = defaults[it] }
    }
}
