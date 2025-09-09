package de.xkript.blackcover.presentation.main

sealed class ViewModelEvent {
    data class OnBuyClick(val productId:String) : ViewModelEvent()
}