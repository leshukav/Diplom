package ru.netology.diplom.activity.event

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diplom.R
import ru.netology.diplom.activity.MainFragment
import ru.netology.diplom.activity.MainFragment.Companion.textArg
import ru.netology.diplom.adapter.*
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.databinding.FragmentEventBinding
import ru.netology.diplom.dto.Event
import ru.netology.diplom.viewmodel.AuthViewModel
import ru.netology.diplom.viewmodel.EventViewModel
import ru.netology.diplom.viewmodel.JobViewModel
import ru.netology.diplom.viewmodel.PostViewModel

@AndroidEntryPoint
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class EventFragment : Fragment() {

    lateinit var binding: FragmentEventBinding
    private lateinit var adapter: EventAdapter
    private val viewModelEvent: EventViewModel by activityViewModels()
    private var playPostId = -1L
    private val authViewModel: AuthViewModel by activityViewModels()
    private val viewModelPost: PostViewModel by activityViewModels()
    private val viewModelJob: JobViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventBinding.inflate(inflater)
        lifecycle.addObserver(MainFragment.observer)
        adapter = EventAdapter(object : OnClick<Event> {
            override fun onClik(event: Event) {
                MainFragment.observer.mediaPlayer?.release()
                MainFragment.observer.mediaPlayer = null
                viewModelPost.loadWallById(event.authorId)
                viewModelPost.loadUserData(event.authorId)
                viewModelJob.loadJobById(event.authorId)
                findNavController().navigate(R.id.authorFragment2)
            }

            override fun onRemove(event: Event) {
                viewModelEvent.removeEventById(event.id)
            }

            override fun onLike(event: Event) {
                if (authViewModel.authorized) {
                    if (!event.likeOwnerIds.contains(authViewModel.data.value?.id)) {
                        viewModelEvent.likeById(event.id)
                    } else {
                        viewModelEvent.unlikeById(event.id)
                    }
                }
            }

            override fun onPlayMusic(event: Event, seekBar: SeekBar) {
                if (!MainFragment.observer.isPaused() || event.id != playPostId) {
                    playPostId = event.id
                    val url = event.attachment?.url
                    MainFragment.observer.apply {
                        mediaPlayer?.reset()
                        mediaPlayer?.setDataSource(url)
                    }.onPlay(seekBar)
                } else {
                    MainFragment.observer.mediaPlayer?.start()
                }
            }

            override fun onPlayVideo(event: Event) {

            }

            override fun onPause() {
                MainFragment.observer.onPause()
            }

            override fun onShare(event: Event) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, event.content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, getString(R.string.share_content))
                startActivity(shareIntent)
            }

            override fun onImage(event: Event) {
                val url = event.attachment?.url
                findNavController().navigate(
                    R.id.action_mainFragment_to_imageFragment3,
                    Bundle().apply {
                        textArg = url
                    })
            }

        }, AppAuth(requireContext()))

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.listEvent.smoothScrollToPosition(0)
                }
            }
        })

        binding.listEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.listEvent.adapter = adapter
        viewModelEvent.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading

            if (state.error) {
                Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        viewModelEvent.loadEvents()
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
            viewModelEvent.data.collectLatest(adapter::submitData)
        }


        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swiperefresh.isRefreshing = it.refresh is LoadState.Loading
                //           || it.append is LoadState.Loading
                //          || it.prepend is LoadState.Loading
            }
        }

        binding.swiperefresh.setOnRefreshListener {
            adapter.refresh()
        }

        authViewModel.state.observe(viewLifecycleOwner) {
            binding.addEvent.isVisible = authViewModel.authorized
        }

        binding.addEvent.setOnClickListener {
            findNavController().navigate(R.id.newEventFragment)
        }

        return binding.root
    }


}