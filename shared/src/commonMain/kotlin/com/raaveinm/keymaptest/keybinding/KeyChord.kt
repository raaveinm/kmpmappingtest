package com.raaveinm.keymaptest.keybinding

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key

///////////////////////////////////////////////
/// Platform hook
///////////////////////////////////////////////

/** Human readable name of a physical key ("S", "Escape", "F5"); the only platform-specific piece. */
expect fun Key.displayName(): String

///////////////////////////////////////////////
/// KeyChord
///////////////////////////////////////////////

/** Keys that are only modifiers: pressing them alone is never a chord. */
private val MODIFIER_KEYS: Set<Key> = setOf(
    Key.CtrlLeft, Key.CtrlRight,
    Key.ShiftLeft, Key.ShiftRight,
    Key.AltLeft, Key.AltRight,
    Key.MetaLeft, Key.MetaRight,
    Key.CapsLock,
)

/**
 * A "key combination": one non-modifier [key] plus the modifiers held at the moment it was pressed.
 * Value-equality is what makes the keymap lookup work, so keep it a plain data class.
 */
data class KeyChord(
    val key: Key,
    val ctrl: Boolean = false,
    val shift: Boolean = false,
    val alt: Boolean = false,
    val meta: Boolean = false,
) {

    /** "Ctrl+Shift+S" on Windows/Linux, "⌃⇧S" on macOS. */
    fun label(isMac: Boolean): String {
        val name: String = key.displayName()
        return if (isMac) {
            buildString {
                if (ctrl) append("⌃")
                if (alt) append("⌥")
                if (shift) append("⇧")
                if (meta) append("⌘")
                append(name)
            }
        } else {
            listOfNotNull(
                "Ctrl".takeIf { ctrl },
                "Alt".takeIf { alt },
                "Shift".takeIf { shift },
                "Meta".takeIf { meta },
                name,
            ).joinToString("+")
        }
    }

    companion object {

        /** Builds a chord from a raw event, or `null` when only a modifier key was pressed. */
        fun from(event: KeyEvent): KeyChord? {
            val key: Key = event.key
            if (key in MODIFIER_KEYS) return null
            return KeyChord(
                key = key,
                ctrl = event.isCtrlPressed,
                shift = event.isShiftPressed,
                alt = event.isAltPressed,
                meta = event.isMetaPressed,
            )
        }

        /** Chord using the platform's "primary" modifier: Cmd on macOS, Ctrl elsewhere. */
        fun primary(
            key: Key,
            isMac: Boolean,
            shift: Boolean = false,
        ): KeyChord = KeyChord(
            key = key,
            ctrl = !isMac,
            meta = isMac,
            shift = shift,
        )
    }
}
