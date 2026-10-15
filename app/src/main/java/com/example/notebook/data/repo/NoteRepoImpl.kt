package com.example.notebook.data.repo

import com.example.notebook.core.AuthService
import com.example.notebook.data.model.Note
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NoteRepoImpl(
    private val collectionRef: CollectionReference
): NoteRepo {
    override fun getAllNotes(isTrashed: Boolean): Flow<List<Note>> = callbackFlow {
        val listener = collectionRef.addSnapshotListener { snapshots, exception ->
            if (exception != null) {
                exception.printStackTrace()
                throw exception
            }
            val notes = mutableListOf<Note>()
            snapshots?.documents?.let { items ->
                for (item in items) {
                    item.data?.let {
                        if (it["inTrash"] != isTrashed) continue
                        if (it["userId"] == AuthService.getUserId()) {
                            notes.add(Note.fromHashMap(it, item.id))
                        }
                    }
                }
                trySend(notes)
            }
        }
        awaitClose {
            listener.remove()
        }
    }
    override suspend fun insertNote(note: Note) {
        collectionRef.add(note.toHashMap()) }

    override suspend fun getNote(id: String): Note? {
        val snapshot = collectionRef.document(id).get().await()
        return snapshot.data?.let {
            Note.fromHashMap(it, id)
        }
    }

    override suspend fun updateNote(id: String, note: Note) {
        collectionRef.document(id).set(note.toHashMap()).await()
    }

    override suspend fun deleteNote(id: String) {
        collectionRef.document(id).delete().await()
    }
}