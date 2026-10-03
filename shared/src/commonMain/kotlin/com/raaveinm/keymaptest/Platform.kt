package com.raaveinm.keymaptest

interface Platform {
    val name: String

    /** `true` on macOS: the "primary" shortcut modifier is Cmd (meta) there, Ctrl everywhere else. */
    val isMac: Boolean
}

expect fun getPlatform(): Platform
