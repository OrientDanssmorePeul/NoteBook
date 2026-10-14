package com.example.notebook.data.repo

import com.example.notebook.data.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepo {
    fun getAllNotes(isTrashed: Boolean = false): Flow<List<Note>>
    suspend fun insertNote(note: Note)
    suspend fun getNote(id: String): Note?
    suspend fun updateNote(id: String, note: Note)
    suspend fun deleteNote(id: String)
}