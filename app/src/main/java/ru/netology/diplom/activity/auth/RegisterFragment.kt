package ru.netology.diplom.activity.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.databinding.FragmentRegisterBinding
import ru.netology.diplom.utils.AndroidUtils
import ru.netology.diplom.viewmodel.AuthViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.netology.diplom.dto.TypeAttachment


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    val authViewModel: AuthViewModel by activityViewModels()

    lateinit var binding: FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val photoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.error),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        val uri = it.data?.data ?: return@registerForActivityResult
                        authViewModel.changeAvatar(uri.toFile(), uri, TypeAttachment.IMAGE)
                    }
                }
            }

        binding.clearBut.setOnClickListener {
            authViewModel.clearAvatar()
            binding.clearBut.isVisible = false
            binding.previewAvatar.setImageResource(R.drawable.person_outline_24)
        }

        binding.previewAvatar.setOnClickListener {
            MaterialAlertDialogBuilder(this.requireContext())
                .setTitle(resources.getString(R.string.choose))
                .setNegativeButton(R.string.gallery) { _, _ ->
                    ImagePicker.with(this)
                        .galleryOnly()
                        .crop()
                        .compress(192)
                        .createIntent(photoLauncher::launch)
                }
                .setPositiveButton(R.string.photo) { _, _ ->
                    ImagePicker.with(this)
                        .cameraOnly()
                        .crop()
                        .compress(192)
                        .createIntent(photoLauncher::launch)
                }
                .show()
        }

        authViewModel.mediaAvatar.observe(viewLifecycleOwner) { mediaAvatar ->
            if (mediaAvatar == null) {
                return@observe
            }
            binding.clearBut.isVisible = true
            binding.previewAvatarContainer.isVisible = true
            binding.previewAvatar.setImageURI(mediaAvatar.uri)
        }


        binding.registrButton.setOnClickListener {
            val name = binding.name.text.toString()
            val login = binding.loginRegistr.text.toString()
            if (!login.isNullOrBlank() || !name.isNullOrBlank()) {
                val password = binding.passRegistr.text.toString()
                val passCheck = binding.passCheckRegistr.text.toString()
                if (password.equals(passCheck)) {
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

                    authViewModel.registration(login, password, name)
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
                binding.loginRegistr.isEnabled = true
                binding.passRegistr.isEnabled = true
                binding.passCheckRegistr.isEnabled = true
                binding.registrButton.isClickable = true
            } else {
                binding.loading.isVisible = false
            }
        }
        authViewModel.data.observe(viewLifecycleOwner) {
            if (authViewModel.authorized) {
                findNavController().navigateUp()
            }
        }

        return binding.root
    }

}