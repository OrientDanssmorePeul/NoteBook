package com.example.notebook.ui.create

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateViewModel(
    private val repo: NoteRepo
): ViewModel() {
    private val _colourNote = MutableStateFlow(PastelPalette.WHITE)

    val colourNote: StateFlow<PastelPalette?> = _colourNote

    fun onNoteColourChange(color: PastelPalette) {
        _colourNote.update { color }
    }

    fun insertNote(title: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertNote(
                Note(
                    title = title,
                    content = content,
                    colour = _colourNote.value
                )
            )
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApp)
                CreateViewModel(app.noteRepo)
            }
        }
    }
}