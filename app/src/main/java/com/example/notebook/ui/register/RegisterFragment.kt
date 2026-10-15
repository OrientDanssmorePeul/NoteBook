package com.example.notebook.ui.register

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.notebook.R
import com.example.notebook.core.AuthService
import com.example.notebook.databinding.FragmentRegisterBinding
import kotlin.toString

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val email = binding.tvEmail.text.toString().trim()
            val password = binding.tvPassword.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) return@setOnClickListener
            AuthService.register(email, password) { success, error ->
                if (success) {
                    findNavController().popBackStack()
                } else {
                    android.widget.Toast.makeText(
                        requireActivity(),
                        error ?: "Registration failed",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            findNavController().popBackStack()
        }

    }

}