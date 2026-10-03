package com.raaveinm.keymaptest.keybinding

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.nativeKeyCode
import java.awt.event.KeyEvent as AwtKeyEvent

// AWT already knows a localized name for every virtual key code ("Escape", "F5", "NumPad-1", ...)
actual fun Key.displayName(): String = AwtKeyEvent.getKeyText(this.nativeKeyCode)
