package com.example.notebook.ui.home

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notebook.data.model.PastelPalette
import com.example.notebook.databinding.FragmentHomeBinding
import com.example.notebook.ui.adapter.DecorationAdapter
import com.example.notebook.ui.adapter.NoteAdapter
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: NoteAdapter
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupListener()

        lifecycleScope.launch {
            viewModel.uiState.collect {
                if (it is HomeUiState.Loading) {

                }
                if (it is HomeUiState.Success) {
                    adapter.setNotes(it.notes)
                    binding.tvNoteCnt.text = "${it.notes.size} Notes"
                }
            }
        }
        lifecycleScope.launch {
            viewModel.sortOption.collect {
                binding.run {
                    when(it) {
                        SortField.ASC -> tvSortDesc.text = "Newest first · tap ⇅ for oldest first"
                        SortField.DESC -> tvSortDesc.text = "Oldest first · tap ⇅ for newest first"
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.filterColour.collect { color ->
                binding.run {
                    cgColors.forEach { view ->
                        if (view is Chip) {
                            if (color == PastelPalette.WHITE) {
                                cgColors.check(cpDefault.id)
                                return@forEach
                            }
                            val viewEntryName = view.context.resources.getResourceEntryName(view.id)
                            view.isChecked = viewEntryName.equals(color.hex.removePrefix("#"), ignoreCase = true)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor = Color.TRANSPARENT
    }

    private fun setupAdapter() {
        adapter = NoteAdapter(emptyList()) { id ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(id)
            findNavController().navigate(action)
        }
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvNotes.adapter = adapter
        binding.rvNotes.layoutManager = layoutManager
        binding.rvNotes.addItemDecoration(DecorationAdapter((7 * resources.displayMetrics.density).toInt()))
    }

    private fun setupListener() {
        binding.run {
            faBtnCreate.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToCreateFragment()
                findNavController().navigate(action)
            }
            btnTrash.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToTrashFragment()
                findNavController().navigate(action)
            }
            etSearch.doAfterTextChanged {
                viewModel.onSearchQueryChanged(it.toString().trim())
            }
            btnSortBy.setOnClickListener {
                if (viewModel.sortOption.value == SortField.ASC) {
                    viewModel.onSortChanged(SortField.DESC)
                } else {
                    viewModel.onSortChanged(SortField.ASC)
                }
            }
            cgColors.setOnCheckedStateChangeListener { group, checkedId ->
                val selectedId = checkedId.firstOrNull()
                if (selectedId != null) {
                    val chipId = group.context.resources.getResourceEntryName(selectedId)
                    if (chipId == "cpDefault") {
                        viewModel.onFilterColourChanged(PastelPalette.WHITE)
                        return@setOnCheckedStateChangeListener
                    }
                    val selectedPalette = PastelPalette.entries.find { enumColor ->
                        enumColor.hex.removePrefix("#").equals(chipId, ignoreCase = true)
                    }
                    if (selectedPalette != null) viewModel.onFilterColourChanged(selectedPalette)
                }
            }
        }
    }
}