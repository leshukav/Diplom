package ru.netology.diplom.activity

import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.diplom.R
import ru.netology.diplom.adapter.Listener
import ru.netology.diplom.adapter.PostAdapter
import ru.netology.diplom.databinding.FragmentMainBinding
import ru.netology.diplom.dto.Post
import ru.netology.diplom.utils.MediaLifecycleObserver
import ru.netology.diplom.viewmodel.PostViewModel

@Suppress("DEPRECATION")
@AndroidEntryPoint
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class MainFragment : Fragment() {
    lateinit var binding: FragmentMainBinding
    lateinit var adapter: PostAdapter
    private val observer = MediaLifecycleObserver()

    private val viewModelPost: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater)
        binding.button.setOnClickListener {
            findNavController().navigate(R.id.authorFragment2)

        }
        lifecycle.addObserver(observer)

        adapter = PostAdapter(object : Listener {
            override fun onClik(post: Post) {
                println(post)
            }

            override fun onRemove(post: Post) {

            }

            override fun onEdit(post: Post) {

            }

            override fun onLike(post: Post) {
                if (!post.likeByMe) {
                    viewModelPost.likeById(post.id)
                } else {
                    viewModelPost.unlikeById(post.id)
                }
            }

            override fun onPlayMusic(post: Post, seekBar: SeekBar) {
                if (observer.isPaused() == false) {
                    val url = post.attachment?.url
                    observer.apply {
                        mediaPlayer?.reset()
                        mediaPlayer?.setDataSource(url)
                    }.onPlay(seekBar)
                } else {
                    observer.mediaPlayer?.start()
                }
            }

            override fun onPlayVideo(post: Post) {

            }

            override fun onPause() {
                observer.onPause()
            }

        })
        binding.listPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.listPosts.adapter = adapter
        //  viewModelPost.loadPosts()
        viewModelPost.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading

            if (state.error) {
                Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        viewModelPost.loadPosts()
                    }
                    .show()
            }
            if (state.likeError) {
                Snackbar.make(binding.root, "You must register", Snackbar.LENGTH_LONG)
                    .setAction("Ok") {
                        findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
                    }
                    .show()
            }
            if (state.removeError) {
                Snackbar.make(binding.root, "Failed to connect. Try later", Snackbar.LENGTH_LONG)
                    .setAction("Ok") {}
                    .show()
            }

            binding.swiperefresh.isRefreshing = state.refreshing

        }

        lifecycleScope.launchWhenCreated {
            viewModelPost.data.collectLatest(adapter::submitData)
        }


        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swiperefresh.isRefreshing = it.refresh is LoadState.Loading
                        || it.append is LoadState.Loading
                        || it.prepend is LoadState.Loading
            }
        }

        binding.swiperefresh.setOnRefreshListener {
            adapter.refresh()
            //    viewModelPost.refreshPosts()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

}