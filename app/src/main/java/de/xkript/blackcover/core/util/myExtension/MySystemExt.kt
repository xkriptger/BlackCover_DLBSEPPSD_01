package de.xkript.blackcover.core.util.myExtension

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.content.ContextCompat
import de.xkript.blackcover.BuildConfig
import de.xkript.blackcover.core.BlackCoverApp
import de.xkript.blackcover.core.util.Constant

fun isNetworkAvailable(): Boolean {
    val connectivityManager =
        BlackCoverApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)      -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)  -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)  -> true
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else                                                        -> false
        }
    }
    else {
        return connectivityManager.activeNetworkInfo?.isConnected ?: false
    }
}

fun hasPermission(permission: String): Boolean = ContextCompat.checkSelfPermission(BlackCoverApp.getInstance(), permission) == PackageManager.PERMISSION_GRANTED
