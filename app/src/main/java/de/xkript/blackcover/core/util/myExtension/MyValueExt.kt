package de.xkript.blackcover.core.util.myExtension

import androidx.core.content.ContextCompat
import de.xkript.blackcover.core.BlackCoverApp

fun getColor(colorId: Int) = ContextCompat.getColor(BlackCoverApp.getInstance(), colorId)

fun getDrawable(drawableId: Int) = ContextCompat.getDrawable(BlackCoverApp.getInstance(), drawableId)

fun getString(stringId: Int) = BlackCoverApp.getInstance().getString(stringId)