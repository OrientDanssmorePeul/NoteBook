package com.example.notebook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notebook.data.model.Note
import com.example.notebook.databinding.ItemFragmentNoteBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class NoteAdapter(
    private var notes: List<Note>,
    private val onClick: (id: String?) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class NoteViewHolder(val binding: ItemFragmentNoteBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note, onClick: (id: String?) -> Unit) {
            binding.run {
                tvNoteTitle.text = note.title
                tvNoteContent.text = note.content
                tvNoteCreatedDate.text = if (note.editedAt != null) formatDate(note.editedAt) else formatDate(note.createdAt)
                mcvNoteCard.setCardBackgroundColor(note.colour.colorInt)
                mcvNoteCard.setOnClickListener {
                    onClick(note.id)
                }
            }
        }
        private fun formatDate(date: Long): String {
            val instant = Instant.ofEpochMilli(date)
            val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
            val formatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
            return localDateTime.format(formatter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemFragmentNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val note = notes[position]
        if (holder is NoteViewHolder) {
            holder.bind(note, onClick)
        }
    }

    override fun getItemCount(): Int = notes.size

    fun setNotes(newCoins: List<Note>) {
        notes = newCoins
        notifyDataSetChanged()
    }
}