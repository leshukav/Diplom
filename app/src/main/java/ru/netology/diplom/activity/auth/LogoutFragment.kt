package ru.netology.diplom.activity.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.activity.MainFragment.Companion.textArg
import ru.netology.diplom.activity.post.PostFragment.Companion.SIGN_IN
import ru.netology.diplom.activity.post.PostFragment.Companion.SIGN_OUT
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

        arguments?.textArg?.let {
            when (it) {
                SIGN_IN -> {
                    binding.questionGroup.isVisible = true
                    true
                }
                SIGN_OUT -> {
                    binding.logoutGroup.isVisible = true
                    true
                }
                else -> false
            }
        }

        binding.buttonOk.setOnClickListener {
            appAuth.removeAuth()
            findNavController().navigate(R.id.action_logoutFragment_to_mainFragment)
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonCancelQuestion.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonOkQuestion.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        return binding.root
    }

}