package com.example.notebook.ui.login

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.notebook.MainActivity
import com.example.notebook.R
import com.example.notebook.core.AuthService
import com.example.notebook.databinding.FragmentLoginBinding
import com.example.notebook.databinding.FragmentRegisterBinding
import kotlin.toString

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.tvEmail.text.toString().trim()
            val password = binding.tvPassword.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) return@setOnClickListener
            AuthService.login(email, password) { success, error ->
                if (success) {
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                } else {
                    android.widget.Toast.makeText(
                        requireActivity(),
                        error ?: "Login failed",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            binding.tvEmail.setText("")
            binding.tvPassword.setText("")
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }
    }
}