package ru.netology.diplom.activity.wall

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.activity.MainFragment.Companion.observer
import ru.netology.diplom.activity.MainFragment.Companion.textArg
import ru.netology.diplom.adapter.WallAdapter
import ru.netology.diplom.adapter.WallListener
import ru.netology.diplom.databinding.FragmentWallBinding
import ru.netology.diplom.dto.Wall
import ru.netology.diplom.viewmodel.PostViewModel

@AndroidEntryPoint
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class WallFragment : Fragment() {
    private lateinit var binding: FragmentWallBinding
    private lateinit var adapter: WallAdapter
    private val viewModelPost: PostViewModel by activityViewModels()
    private var playPostId = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWallBinding.inflate(inflater, container, false)
        adapter = WallAdapter(object : WallListener {

            override fun onRemove(wall: Wall) {
                viewModelPost.removeWallPostDao(wall.id)
                viewModelPost.removePostById(wall.id)
            }

            override fun onLike(wall: Wall) {
                if (!wall.likeByMe) {
                    viewModelPost.likeById(wall.id)
                } else {
                    viewModelPost.unlikeById(wall.id)
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

            override fun onPause() {
                observer.onPause()
            }

            override fun onShare(wall: Wall) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, wall.content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, getString(R.string.share_content))
                startActivity(shareIntent)
            }

            override fun onImage(wall: Wall) {
                val url = wall.attachment?.url
                findNavController().navigate(
                    R.id.action_authorFragment2_to_imageFragment3,
                    Bundle().apply {
                        textArg = url
                    })
            }
        })
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter
        viewModelPost.wallData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
    }


}
