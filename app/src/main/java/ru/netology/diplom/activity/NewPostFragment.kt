package ru.netology.diplom.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuHost
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.diplom.R
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.databinding.FragmentNewPostBinding
import ru.netology.diplom.utils.AndroidUtils
import ru.netology.diplom.viewmodel.PostViewModel

@AndroidEntryPoint
@OptIn(ExperimentalCoroutinesApi::class)
class NewPostFragment : Fragment(), MenuProvider {
    lateinit var binding: FragmentNewPostBinding
    val pickPhotoLauncher = pickPhotoLauncher()
    private val viewModelPost: PostViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPostBinding.inflate(inflater, container, false)

        binding.clear.setOnClickListener {
            viewModelPost.clearPhoto()
        }

        viewModelPost.media.observe(viewLifecycleOwner) { media ->
            if (media == null) {
                binding.photoContainer.isGone = true
                return@observe
            }
            binding.photoContainer.isVisible = true
            binding.clear.isVisible = true
            binding.preview.setImageURI(media.uri)
        }
        binding.edit.requestFocus()



        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_new_post, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
        when (menuItem.itemId) {
            R.id.save -> {
                val text = binding.edit.text.toString()
                if (text.isNotBlank()) {
                    this@NewPostFragment.viewModelPost.savePost(text)
                }
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
                true
            }
            R.id.photo -> {
                ImagePicker.with(this)
                    .crop()
                    .compress(2048)
                    .provider(ImageProvider.CAMERA)
                    .createIntent(pickPhotoLauncher::launch)
                true
            }
            R.id.gallary -> {
                ImagePicker.with(this)
                    .crop()
                    .compress(2048)
                    .provider(ImageProvider.GALLERY)
                    .galleryMimeTypes(
                        arrayOf(
                            "image/png",
                            "image/jpeg",
                        )
                    )
                    .createIntent(pickPhotoLauncher::launch)
                true
            }
            R.id.media -> {
                true
            }
            else -> false
        }
    private fun pickPhotoLauncher(): ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            ImagePicker.RESULT_ERROR -> {
                Snackbar.make(
                    binding.root,
                    ImagePicker.getError(it.data),
                    Snackbar.LENGTH_LONG
                ).show()
            }
            Activity.RESULT_OK -> {
                val uri: Uri? = it.data?.data
                uri?.toFile()?.let { it1 -> viewModelPost.changePhoto(uri, it1) }
            }
        }
    }
}
