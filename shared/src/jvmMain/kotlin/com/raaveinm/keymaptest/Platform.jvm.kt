package com.raaveinm.keymaptest

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val isMac: Boolean = System.getProperty("os.name").orEmpty().lowercase().contains("mac")
}

actual fun getPlatform(): Platform = JVMPlatform()
