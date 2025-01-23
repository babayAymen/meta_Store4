package com.aymen.metastore.model.repository.ViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StateHandlerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {


    companion object {
        private const val KEY_SCROLL_POSITION = "scroll_position"
    }

    // Save scroll position
    fun saveScrollPosition(position: Int) {
        savedStateHandle[KEY_SCROLL_POSITION] = position
    }

    // Retrieve scroll position
    fun getScrollPosition(): Int {
        return savedStateHandle[KEY_SCROLL_POSITION] ?: 0
    }
}