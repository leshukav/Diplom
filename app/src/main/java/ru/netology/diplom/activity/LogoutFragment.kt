package ru.netology.diplom.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.databinding.FragmentLogoutBinding
import javax.inject.Inject

@AndroidEntryPoint
class LogoutFragment : DialogFragment() {

    @Inject
    lateinit var appAuth: AppAuth
    lateinit var binding: FragmentLogoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogoutBinding.inflate(inflater, container, false)

        binding.buttonOk.setOnClickListener {
            appAuth.removeAuth()
            findNavController().navigate(R.id.action_logoutFragment_to_mainFragment)
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

}