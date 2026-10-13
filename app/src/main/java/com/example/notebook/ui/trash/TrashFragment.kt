package com.example.notebook.ui.trash

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notebook.R
import com.example.notebook.databinding.FragmentTrashBinding

class TrashFragment : Fragment() {

    private lateinit var binding: FragmentTrashBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTrashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}