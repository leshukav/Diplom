package ru.netology.diplom.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diplom.R
import ru.netology.diplom.adapter.Listener
import ru.netology.diplom.adapter.PostAdapter
import ru.netology.diplom.databinding.FragmentMainBinding
import ru.netology.diplom.dto.Post
import ru.netology.diplom.utils.MediaLifecycleObserver
import ru.netology.diplom.viewmodel.AuthViewModel
import ru.netology.diplom.viewmodel.PostViewModel
import ru.netology.diplom.utils.StringArgs

@AndroidEntryPoint
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class MainFragment : Fragment(), MenuProvider {
    companion object {
        var Bundle.textArg by StringArgs
        val observer = MediaLifecycleObserver()
    }

    lateinit var binding: FragmentMainBinding
    lateinit var adapter: PostAdapter
    private var playPostId = -1L
    private val viewModelPost: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater)
        lifecycle.addObserver(observer)

        adapter = PostAdapter(object : Listener {
            override fun onClik(post: Post) {
                observer.mediaPlayer?.release()
                observer.mediaPlayer = null
                viewModelPost.loadWallById(post.authorId)
                viewModelPost.loadUserData(post.authorId)
                findNavController().navigate(R.id.authorFragment2)

            }

            override fun onRemove(post: Post) {
                viewModelPost.removePostById(post.id)
            }

            override fun onLike(post: Post) {
                if (!post.likeByMe) {
                    viewModelPost.likeById(post.id)
                } else {
                    viewModelPost.unlikeById(post.id)
                }
            }

            override fun onPlayMusic(post: Post, seekBar: SeekBar) {
                if (!observer.isPaused() || post.id != playPostId) {
                    playPostId = post.id
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

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, getString(R.string.share_content))
                startActivity(shareIntent)
            }

            override fun onImage(post: Post) {
                val url = post.attachment?.url
                findNavController().navigate(
                    R.id.action_mainFragment_to_imageFragment3,
                    Bundle().apply {
                        textArg = url
                    })
            }

        })

        val dividerItemDecoration = DividerItemDecoration(this.context, RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider_drawable))
        binding.listPosts.addItemDecoration(dividerItemDecoration)


        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.listPosts.smoothScrollToPosition(0)
                }
            }
        })

        binding.listPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.listPosts.adapter = adapter
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
        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_auth, menu)
        menu.setGroupVisible(R.id.unauthorization, !authViewModel.authorized)
        menu.setGroupVisible(R.id.authorization, authViewModel.authorized)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
        when (menuItem.itemId) {
            R.id.login -> {
                findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
                true
            }
            R.id.register -> {
                findNavController().navigate(R.id.registerFragment)
                true
            }
            R.id.logout -> {
                findNavController().navigate(R.id.logoutFragment)
                true
            }
            R.id.addPost -> {
                findNavController().navigate(R.id.newPostFragment)
                true
            }
            else -> false
        }

}