package com.example.keymappertest.keybinding

actual val IS_APPLE: Boolean get() = System.getProperty("os.name").orEmpty().lowercase().contains("mac")