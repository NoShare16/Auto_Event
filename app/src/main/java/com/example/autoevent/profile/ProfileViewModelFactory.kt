package com.example.autoevent.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Erstellt ViewModels, die genau einen String-Parameter erwarten.
 *
 * Verwendung:
 *   val vm: OtherProfileViewModel = viewModel(
 *       factory = ProfileViewModelFactory(targetUid)
 *   )
 */
class ProfileViewModelFactory(
    private val uid: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            OtherProfileViewModel::class.java ->
                OtherProfileViewModel(uid) as T    //  ⬅️ dein ViewModel
            else ->
                throw IllegalArgumentException("Unknown ViewModel: $modelClass")
        }
    }
}
