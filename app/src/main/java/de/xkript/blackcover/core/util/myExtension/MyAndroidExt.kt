package de.xkript.blackcover.core.util.myExtension

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.util.Log
import de.xkript.blackcover.BuildConfig
import de.xkript.blackcover.core.util.Constant

fun isDevFlaver(): Boolean = BuildConfig.FLAVOR == Constant.FLAVOR_DEV

@Suppress("DEPRECATION")
fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
    return (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it -> it.service.className == service.name }
}

fun log(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d("DEBUG_LOGGER", message)
    }
}