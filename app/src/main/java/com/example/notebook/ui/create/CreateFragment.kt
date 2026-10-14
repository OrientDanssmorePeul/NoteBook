package com.example.notebook.ui.create

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.notebook.data.model.PastelPalette
import com.example.notebook.databinding.FragmentCreateBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class CreateFragment : Fragment() {

    private lateinit var binding: FragmentCreateBinding

    private val viewModel: CreateViewModel by viewModels { CreateViewModel.factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCreateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        lifecycleScope.launch {
            viewModel.colourNote.collect {
                if (it != null) bindColourNoteData(it)
            }
        }
    }

    private fun setupListener() {
        binding.run {
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            cgColors.setOnCheckedStateChangeListener   { group, checkedIds ->
                val selectedId = checkedIds.firstOrNull()
                if (selectedId != null) {
                    val chipId = group.context.resources.getResourceEntryName(selectedId)
                    val selectedPalette = PastelPalette.entries.find { enumColor ->
                        enumColor.hex.removePrefix("#").equals(chipId, ignoreCase = true)
                    }
                    if (selectedPalette != null) viewModel.onNoteColourChange(selectedPalette)
                }
            }
            btnSave.setOnClickListener {
                viewModel.insertNote(
                    etNoteTitle.text.toString(),
                    etNoteContent.text.toString()
                )
                findNavController().popBackStack()
            }
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
            clWrapper.setBackgroundColor(resolvedColor)
            requireActivity().window.statusBarColor = resolvedColor
        }
    }

}