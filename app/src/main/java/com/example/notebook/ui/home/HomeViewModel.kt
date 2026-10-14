package com.example.notebook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notebook.MyApp
import com.example.notebook.data.model.Note
import com.example.notebook.data.model.PastelPalette
import com.example.notebook.data.repo.NoteRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: NoteRepo
): ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    private val _searchText = MutableStateFlow("")
    private val _sortOption = MutableStateFlow(SortField.ASC)
    private val _filterColour = MutableStateFlow(PastelPalette.WHITE)
    private val _isLoading = MutableStateFlow(false)
    val filterColour: StateFlow<PastelPalette> = _filterColour
    val sortOption: StateFlow<SortField> = _sortOption
    val uiState: StateFlow<HomeUiState?> = combine(
        _notes,
        _searchText,
        _sortOption,
        _filterColour,
        _isLoading
    ) { items, text, sortField, filterColour ,isLoading ->
        if (isLoading) {
            HomeUiState.Loading
        } else {
            val items = filterItems(items, text, sortField, filterColour)
            HomeUiState.Success (
                items,
                items.isEmpty()
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeUiState.Loading
    )

    init {
        getNotes()
    }

    fun filterItems(items: List<Note>, text: String, sortField: SortField, filterColour: PastelPalette): List<Note> {
        var filteredItems = items.filter { it.title.contains(text, ignoreCase = true) }
        filteredItems = if (filterColour != PastelPalette.WHITE) {
            filteredItems.filter { it.colour.colorInt == filterColour.colorInt }
        } else {
            filteredItems
        }
        filteredItems = when(sortField) {
            SortField.ASC -> filteredItems.sortedBy { it.createdAt }
            SortField.DESC -> filteredItems.sortedByDescending {it.createdAt }
        }
        return filteredItems
    }
    fun getNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllNotes().collect { items ->
                _notes.update { items }
            }
        }
    }
    fun onSortChanged(sortField: SortField) {
        _sortOption.update {sortField}
    }
    fun onSearchQueryChanged(text: String) {
        _searchText.update { text }
    }
    fun onFilterColourChanged(colour: PastelPalette) {
        _filterColour.update { colour }
    }
    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApp)
                HomeViewModel(app.noteRepo)
            }
        }
    }
}


sealed interface HomeUiState {
    object Loading: HomeUiState
    data class Error(val message: String): HomeUiState
    data class Success(val notes: List<Note>, val isEmpty: Boolean): HomeUiState
}

enum class SortField{
    ASC, DESC
}