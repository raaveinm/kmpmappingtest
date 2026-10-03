package com.raaveinm.keymaptest

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    val appState: AppState = remember { AppState() }

    Window(
        onCloseRequest = ::exitApplication,
        title = appTitle(),
        onPreviewKeyEvent = appState::onKeyEvent, // window-level: catches keys regardless of which widget has focus
    ) {
        App(state = appState)
    }
}
