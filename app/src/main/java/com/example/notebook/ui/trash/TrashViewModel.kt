package com.example.notebook.ui.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notebook.MyApp
import com.example.notebook.data.model.Note
import com.example.notebook.data.repo.NoteRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrashViewModel(
    private val repo: NoteRepo
): ViewModel() {

    private val _deletes = MutableStateFlow<List<Note>>(emptyList())
    val deletes: StateFlow<List<Note>> = _deletes

    init {
        getDeletes()
    }

    fun getDeletes() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllNotes(true).collect { items ->
                _deletes.update { items }
            }
        }
    }

    fun restoreNote(id: String) {
        viewModelScope.launch {
            val note = deletes.value.find { it.id == id }
            if (note == null) return@launch
            repo.updateNote(id, note.copy( inTrash = false ))
            getDeletes()
        }
    }
    fun deleteNote(id: String) {
        viewModelScope.launch {
            repo.deleteNote(id)
        }
    }
    fun deleteAllNote() {
        viewModelScope.launch {
            for (note in _deletes.value) {
                if (note.id != null) {
                    deleteNote(note.id)
                }
            }
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApp)
                TrashViewModel(app.noteRepo)
            }
        }
    }
}