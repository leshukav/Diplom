package ru.netology.diplom.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.databinding.FragmentLoginBinding
import ru.netology.diplom.databinding.FragmentRegisterBinding
import ru.netology.diplom.utils.AndroidUtils
import ru.netology.diplom.viewmodel.AuthViewModel

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    val authViewModel: AuthViewModel by activityViewModels()

    lateinit var binding: FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.registrButton.setOnClickListener {
            val name = binding.name.text.toString()
            val login = binding.loginRegistr.text.toString()
            if (!login.isNullOrBlank() || !name.isNullOrBlank()) {
                val pass = binding.passRegistr.text.toString()
                val passCheck = binding.passCheckRegistr.text.toString()
                if (pass.equals(passCheck)) {
                    AndroidUtils.hideKeyboard(requireView())
                    with(binding) {
                        registrButton.let { button ->
                            button.isClickable = false
                            button.text = "Wait_authorization"
                        }
                        loading.isVisible = true
                        loginRegistr.isEnabled = false
                        passRegistr.isEnabled = false
                        passCheckRegistr.isEnabled = false
                    }

                    authViewModel.registration(login, pass, name)
                } else {
                    Toast.makeText(context, R.string.Password_not_correct, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, R.string.Login_Name_is_not_empty, Toast.LENGTH_LONG).show()

            }
        }
        authViewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.errorCode) {
                binding.loading.isVisible = false
                Toast.makeText(context, R.string.Login_not_found, Toast.LENGTH_LONG).show()
            } else {
                binding.loading.isVisible = false
            }
        }
        authViewModel.data.observe(viewLifecycleOwner) {
            if (authViewModel.authorized ) {
                findNavController().navigateUp() //.navigate(R.id.fragment_main)
            }
        }


        return binding.root
    }

}