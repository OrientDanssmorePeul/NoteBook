package com.example.notebook.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notebook.data.model.Note
import com.example.notebook.databinding.ItemFragmentTrashBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class DeletedNoteAdapter(
    private var deletes: List<Note>,
    private val onClick: (id: String?, action: String) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class DeletedViewHolder(val binding: ItemFragmentTrashBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note, onClick: (id: String?, action: String) -> Unit) {
            binding.run {
                vColorIndic.backgroundTintList = ColorStateList.valueOf(note.colour.colorInt)
                tvNoteTitle.text = note.title
                tvNoteDateDelete.text = if (note.deletedAt != null) formatDate(note.deletedAt) else "Unavailable"
                btnDelete.setOnClickListener {
                    onClick(note.id, "delete")
                }
                btnRestore.setOnClickListener {
                    onClick(note.id, "restore")
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
        val binding = ItemFragmentTrashBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeletedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val deleted = deletes[position]
        if (holder is DeletedViewHolder) {
            holder.bind(deleted, onClick)
        }
    }

    override fun getItemCount(): Int = deletes.size

    fun setDeleted(newDeleted: List<Note>) {
        deletes = newDeleted
        notifyDataSetChanged()
    }
}