package com.example.notebook

import android.app.Application
import com.example.notebook.data.repo.NoteRepo
import com.example.notebook.data.repo.NoteRepoImpl
import com.google.firebase.firestore.FirebaseFirestore

class MyApp: Application() {
    lateinit var noteRepo: NoteRepo
    override fun onCreate() {
        super.onCreate()

        noteRepo = NoteRepoImpl(
            FirebaseFirestore.getInstance().collection("notes")
        )
    }
}