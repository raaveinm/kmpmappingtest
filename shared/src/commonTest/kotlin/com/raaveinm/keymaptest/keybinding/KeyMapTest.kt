package com.raaveinm.keymaptest.keybinding

import androidx.compose.ui.input.key.Key
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class KeyMapTest {

    private val keyMap: KeyMap = KeyMap(defaultBindings(isMac = false))

    @Test
    fun defaultChordResolvesToAction() {
        assertEquals(AppAction.Save, keyMap.resolve(KeyChord(Key.S, ctrl = true)))
    }

    @Test
    fun modifiersMatter() {
        assertEquals(AppAction.SaveAll, keyMap.resolve(KeyChord(Key.S, ctrl = true, shift = true)))
        assertNull(keyMap.resolve(KeyChord(Key.S)))
    }

    @Test
    fun rebindMovesChordAndFreesTheOldOne() {
        val newChord = KeyChord(Key.K, ctrl = true)
        assertNull(keyMap.rebind(AppAction.Save, newChord))
        assertEquals(AppAction.Save, keyMap.resolve(newChord))
        assertNull(keyMap.resolve(KeyChord(Key.S, ctrl = true)))
    }

    @Test
    fun rebindToTakenChordDisplacesOwner() {
        val displaced: AppAction? = keyMap.rebind(AppAction.Save, KeyChord(Key.O, ctrl = true))
        assertEquals(AppAction.OpenFile, displaced)
        assertNull(keyMap.chordFor(AppAction.OpenFile))
        assertEquals(AppAction.Save, keyMap.resolve(KeyChord(Key.O, ctrl = true)))
    }

    @Test
    fun resetRestoresDefaults() {
        keyMap.unbind(AppAction.Help)
        keyMap.rebind(AppAction.Save, KeyChord(Key.K, ctrl = true))
        keyMap.resetAll()
        assertEquals(KeyChord(Key.F1), keyMap.chordFor(AppAction.Help))
        assertEquals(KeyChord(Key.S, ctrl = true), keyMap.chordFor(AppAction.Save))
    }
}
