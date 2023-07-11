package ru.netology.diplom.activity.post

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.MediaController
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.diplom.R
import ru.netology.diplom.databinding.FragmentNewPostBinding
import ru.netology.diplom.dto.TypeAttachment
import ru.netology.diplom.utils.AndroidUtils
import ru.netology.diplom.utils.FileFromContentUri
import ru.netology.diplom.viewmodel.PostViewModel
import java.io.File


@AndroidEntryPoint
@OptIn(ExperimentalCoroutinesApi::class)
class NewPostFragment : Fragment(), MenuProvider {
    private lateinit var binding: FragmentNewPostBinding
    private val fileUtils = FileFromContentUri()
    private val pickAudioLauncher = pickMediaLauncher(TypeAttachment.AUDIO)
    private val pickPhotoLauncher = pickMediaLauncher(TypeAttachment.IMAGE)
    private val pickVideoLauncher = pickMediaLauncher(TypeAttachment.VIDEO)
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
            viewModelPost.clearMedia()
        }

        viewModelPost.media.observe(viewLifecycleOwner) { media ->
            with(binding){
                if (media == null) {
                    photoContainer.isGone = true
                    return@observe
                }
                photoContainer.isVisible = true
                clear.isVisible = true
                when (media.type) {
                    TypeAttachment.IMAGE -> {
                        videoPreview.isVisible = false
                        fabPlay.isVisible = false
                        preview.isVisible = true
                        preview.setImageURI(media.uri)
                    }
                    TypeAttachment.AUDIO -> {
                        preview.isVisible = true
                        videoPreview.isVisible = false
                        fabPlay.isVisible = false
                        preview.setImageResource(R.drawable.music_logo)
                    }

                    TypeAttachment.VIDEO -> {
                            preview.isVisible = false
                            videoPreview.isVisible = true
                            fabPlay.isVisible = true
                            videoPreview.setVideoURI(media.uri)
                            videoPreview.seekTo(10)
                            fabPlay.setOnClickListener {
                                fabPlay.isVisible = false
                                videoPreview.apply {
                                    setMediaController(MediaController(context))
                                    setVideoURI(media.uri)
                                    setOnPreparedListener {
                                        start()
                                    }
                                    setOnCompletionListener {
                                        stopPlayback()
                                        fabPlay.isVisible = true
                                    }
                                }
                            }
                    }
                }
            }

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
                viewModelPost.clearMedia()
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
            R.id.audio -> {
                val intent = Intent().apply {
                    action = Intent.ACTION_GET_CONTENT
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "audio/*"
                }
                pickAudioLauncher.launch(intent)
                true
            }
            R.id.video -> {
                val intent = Intent().apply {
                    action = Intent.ACTION_GET_CONTENT
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "video/*"
                }
                pickVideoLauncher.launch(intent)
                true
            }
            else -> false
        }

    private fun pickMediaLauncher(type: TypeAttachment): ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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
                    if (type == TypeAttachment.IMAGE) {
                            uri?.toFile()?.let { file -> viewModelPost.changeMedia(uri, file, type) }
                    } else {
                    val file = context?.let {
                        if (uri != null) {
                            fileUtils.uriToFile(it, uri)
                        } else File("errorName")
                    }
                    val uriFile = Uri.fromFile(file)
                    if (file != null) {
                        viewModelPost.changeMedia(uriFile, file, type)
                    }
                }
                }
                Activity.RESULT_CANCELED -> {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.contracts_is_canceled),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
}

