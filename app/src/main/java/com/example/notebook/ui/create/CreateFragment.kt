package com.example.notebook.ui.create

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notebook.R
import com.example.notebook.databinding.FragmentCreateBinding

class CreateFragment : Fragment() {

    private lateinit var binding: FragmentCreateBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCreateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}