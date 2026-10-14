package com.example.notebook.ui.trash

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notebook.R
import com.example.notebook.databinding.FragmentTrashBinding
import com.example.notebook.ui.adapter.DeletedNoteAdapter
import kotlinx.coroutines.launch

class TrashFragment : Fragment() {

    private lateinit var binding: FragmentTrashBinding
    private val viewModel: TrashViewModel by viewModels { TrashViewModel.factory }
    private lateinit var adapter: DeletedNoteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTrashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupListener()

        lifecycleScope.launch {
            viewModel.deletes.collect {
                adapter.setDeleted(it)
            }
        }
    }

    private fun setupAdapter() {
        adapter = DeletedNoteAdapter(emptyList()) { id, action ->
            if (id != null) {
                if (action == "restore") viewModel.restoreNote(id)
                if (action == "delete") viewModel.deleteNote(id)
            }
        }
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvDeletedNotes.adapter = adapter
        binding.rvDeletedNotes.layoutManager = layoutManager
    }
    private fun setupListener() {
        binding.run {
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnEmpty.setOnClickListener {
                viewModel.deleteAllNote()
            }
        }
    }
}