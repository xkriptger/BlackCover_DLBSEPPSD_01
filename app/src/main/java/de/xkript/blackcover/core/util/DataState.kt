package de.xkript.blackcover.core.util

sealed class DataState<out T> {
    class Success<out D>(val data: D) : DataState<D>()
    class Error(val code: Int, val message: String) : DataState<Nothing>()
    data object Loading : DataState<Nothing>()
}