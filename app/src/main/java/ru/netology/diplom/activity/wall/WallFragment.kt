package ru.netology.diplom.activity.wall

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.activity.MainFragment.Companion.observer
import ru.netology.diplom.activity.MainFragment.Companion.textArg
import ru.netology.diplom.activity.post.PostFragment
import ru.netology.diplom.adapter.WallAdapter
import ru.netology.diplom.adapter.OnClick
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.databinding.FragmentWallBinding
import ru.netology.diplom.dto.Wall
import ru.netology.diplom.viewmodel.AuthViewModel
import ru.netology.diplom.viewmodel.PostViewModel
import ru.netology.diplom.viewmodel.WallViewModel

@AndroidEntryPoint
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class WallFragment : Fragment() {
    private lateinit var binding: FragmentWallBinding
    private lateinit var adapter: WallAdapter
    private val viewModelPost: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val wallViewModel: WallViewModel by activityViewModels()
    private var playPostId = -1L
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWallBinding.inflate(inflater, container, false)
        adapter = WallAdapter(object : OnClick<Wall> {

            override fun onRemove(wall: Wall) {
                wallViewModel.removeWallPostDao(wall.id)
                viewModelPost.removePostById(wall.id)
            }

            override fun onLike(wall: Wall) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = PostFragment.SIGN_IN
                        })
                } else {
                    if (!wall.likeOwnerIds.contains(authViewModel.data.value?.id)) {
                        viewModelPost.likeById(wall.id)
                        wallViewModel.likeByIdWall(wall.id)
                    } else {
                        viewModelPost.unlikeById(wall.id)
                        wallViewModel.unlikeByIdWall(wall.id)
                    }
                }
            }

            override fun onPlayMusic(wall: Wall, seekBar: SeekBar) {
                if (!observer.isPaused() || wall.id != playPostId) {
                    playPostId = wall.id
                    val url = wall.attachment?.url
                    observer.apply {
                        mediaPlayer?.reset()
                        mediaPlayer?.setDataSource(url)
                    }.onPlay(seekBar)
                } else {
                    observer.mediaPlayer?.start()
                }
            }

            override fun onPlayVideo(wall: Wall) {
            }

            override fun onClik(data: Wall) {

            }

            override fun onPause() {
                observer.onPause()
            }

            override fun onShare(wall: Wall) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = PostFragment.SIGN_IN
                        })
                } else {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, wall.content)
                        type = "text/plain"
                    }
                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.share_content))
                    startActivity(shareIntent)
                }
            }

            override fun onImage(wall: Wall) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = PostFragment.SIGN_IN
                        })
                } else {
                    val url = wall.attachment?.url
                    findNavController().navigate(
                        R.id.action_authorFragment2_to_imageFragment3,
                        Bundle().apply {
                            textArg = url
                        })
                }
            }
        }, AppAuth(requireContext()))
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter
        wallViewModel.wallData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (it.isEmpty()) {
                binding.wallEmpty.isVisible = true
                binding.wallEmpty.text = getString(R.string.post_not_found)
            }
        }

        wallViewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading

            if (state.loadError) {
                Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        viewModelPost.loadPosts()
                    }
                    .show()
            }
            if (state.likeError) {
                Snackbar.make(
                    binding.root, "\n" +
                            getString(R.string.you_can_t), Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.ok) {}
                    .show()
            }
            if (state.removeError) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.failed_to_connect),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.ok) {}
                    .show()
            }
        }

        return binding.root
    }


}
