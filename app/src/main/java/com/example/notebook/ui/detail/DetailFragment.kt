package com.example.notebook.ui.detail

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notebook.R
import com.example.notebook.data.model.Note
import com.example.notebook.data.model.PastelPalette
import com.example.notebook.databinding.FragmentDetailBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private val viewModel: DetailViewModel by viewModels { DetailViewModel.factory }
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        loadNoteDetail()

        lifecycleScope.launch {
            viewModel.noteRead.collect {
                if (it is DetailUiState.Loading) {

                }
                if (it is DetailUiState.Success) {
                    bindReadNoteData(it.note)
                    if (binding.etNoteTitle.text.isNullOrEmpty() &&
                        binding.etNoteContent.text.isNullOrEmpty()) {
                        bindWriteNoteData(it.note)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.isEditing.collect {
                if (it) binding.mlWrapper.transitionToState(R.id.end) else  binding.mlWrapper.transitionToState(R.id.start)
            }
        }
        lifecycleScope.launch {
            viewModel.colourNote.collect {
                bindColourNoteData(it)
            }
        }
    }

    private fun setupListener() {
        binding.run {
            mlWrapper.setTransitionListener(
                object : MotionLayout.TransitionListener {
                    override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                        if (currentId == R.id.start) {
                            binding.vsNoteTitleSwitch.displayedChild = 0
                            binding.vsNoteContentSwitcher.displayedChild = 0
                            binding.etNoteTitle.clearFocus()
                            viewModel.onIsEditingChange(false)
                            bindWriteNoteData(viewModel.noteCopy)
                        }
                        if (currentId == R.id.end) {
                            binding.vsNoteTitleSwitch.displayedChild = 1
                            binding.vsNoteContentSwitcher.displayedChild = 1
                            viewModel.onIsEditingChange(true)
                        }
                    }
                    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
                    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
                    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
                }
            )
            cgColors.setOnCheckedStateChangeListener  { group, checkedIds ->
                val selectedId = checkedIds.firstOrNull()
                if (selectedId != null) {
                    val chipId = group.context.resources.getResourceEntryName(selectedId)
                    val selectedPalette = PastelPalette.entries.find { enumColor ->
                        enumColor.hex.removePrefix("#").equals(chipId, ignoreCase = true)
                    }
                    if (selectedPalette != null) viewModel.onNoteColourChange(selectedPalette)
                }
            }
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnTrash.setOnClickListener {
                val action = DetailFragmentDirections.actionDetailFragmentToTrashFragment()
                findNavController().navigate(action)
            }
            btnUpdate.setOnClickListener {
                etNoteContent.text.toString()
                if (args.noteId != null) {
                    viewModel.updateNote(
                        args.noteId!!,
                        etNoteTitle.text.toString(),
                        etNoteContent.text.toString()
                    )
                }
            }
            btnRemove.setOnClickListener {
                if (args.noteId != null) {
                    viewModel.deleteNote(args.noteId!!)
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun bindReadNoteData(note: Note?) {
        if (note == null) return
        binding.run {
            tvNoteTitle.text = note.title
            tvNoteContent.text = note.content
            tvNoteDate.text = if (note.editedAt != null ) "Edited ${formatDate(note.editedAt)}" else "Created ${formatDate(note.createdAt)}"
        }
    }
    private fun bindWriteNoteData(note: Note?) {
        if (note == null) return
        binding.run {
            etNoteTitle.setText(note.title)
            etNoteContent.setText(note.content)
            bindColourNoteData(note.colour)
        }
    }
    private fun bindColourNoteData(palette: PastelPalette) {
        binding.run {
            val resolvedColor = palette.colorInt
            cgColors.forEach { view ->
                if (view is Chip) {
                    val viewEntryName = view.context.resources.getResourceEntryName(view.id)
                    view.isChecked = viewEntryName.equals(palette.hex.removePrefix("#"), ignoreCase = true)
                }
            }
            mlWrapper.setBackgroundColor(resolvedColor)
            requireActivity().window.statusBarColor = resolvedColor
        }
    }
    private fun loadNoteDetail() {
        lifecycleScope.launch {
            if (args.noteId != null) {
                viewModel.getNoteDetail(args.noteId!!)
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