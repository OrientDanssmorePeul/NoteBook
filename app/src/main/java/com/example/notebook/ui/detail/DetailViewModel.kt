package com.example.notebook.ui.detail

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

class DetailViewModel(
    private val repo: NoteRepo
): ViewModel() {
    private val _note = MutableStateFlow<Note?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isEditing = MutableStateFlow(false)
    private val _colourNote = MutableStateFlow(PastelPalette.WHITE)
    val isEditing: StateFlow<Boolean> = _isEditing
    val colourNote: StateFlow<PastelPalette> =  _colourNote
    var noteCopy: Note? = null
    val noteRead: StateFlow<DetailUiState> = combine(
        _note,
        _isLoading
    ) { note, isLoading ->
        if (isLoading) {
            DetailUiState.Loading
        } else {
            DetailUiState.Success (
                note,
                note == null
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DetailUiState.Loading
    )

    fun getNoteDetail(id: String) {
        if (_note.value?.id == id && isEditing.value) return
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            repo.getNote(id).let { note ->
                _note.update { note }
                noteCopy = note
                _colourNote.update { note?.colour ?: PastelPalette.WHITE}
            }
            _isLoading.update { false }
        }
    }
    fun updateNote(id: String, title: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateNote(
                id,
                noteCopy!!.copy(
                    title = title,
                    content = content,
                    colour = _colourNote.value,
                    editedAt = System.currentTimeMillis()
                )
            )
            _isEditing.update { false }
            getNoteDetail(id)
        }
    }
    fun deleteNote(id: String) {
        viewModelScope.launch {
            repo.updateNote(id, noteCopy!!.copy(deletedAt = System.currentTimeMillis(), inTrash = true))
        }
    }
    fun onIsEditingChange(isEditing: Boolean) {
        _isEditing.update { isEditing }
    }
    fun onNoteColourChange(color: PastelPalette) {
        _colourNote.update { color }
    }


    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApp)
                DetailViewModel(app.noteRepo)
            }
        }
    }
}

sealed interface DetailUiState {
    object Loading: DetailUiState
    data class Success(val note: Note?, val isEmpty: Boolean): DetailUiState
    data class Error(val message: String): DetailUiState
}