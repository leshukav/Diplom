package ru.netology.diplom.activity.event

import android.annotation.SuppressLint
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
import ru.netology.diplom.activity.post.PostFragment
import ru.netology.diplom.adapter.*
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.databinding.FragmentEventBinding
import ru.netology.diplom.dto.Event
import ru.netology.diplom.viewmodel.*
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class EventFragment() : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth
    lateinit var binding: FragmentEventBinding
    private lateinit var adapter: EventAdapter
    private val viewModelEvent: EventViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val wallViewModel: WallViewModel by activityViewModels()
    private val viewModelJob: JobViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventBinding.inflate(inflater)
        adapter = EventAdapter(object : OnClick<Event> {
            override fun onClik(event: Event) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = PostFragment.SIGN_IN
                        })
                } else {
                    MainFragment.observer.mediaPlayer?.release()
                    MainFragment.observer.mediaPlayer = null
                    wallViewModel.loadWallById(event.authorId)
                    userViewModel.loadUserData(event.authorId)
                    viewModelJob.loadJobById(event.authorId)
                    findNavController().navigate(R.id.authorFragment2)
                }
            }

            override fun onRemove(event: Event) {
                viewModelEvent.removeEventById(event.id)
            }

            override fun onLike(event: Event) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = PostFragment.SIGN_IN
                        })
                } else {
                    if (authViewModel.authorized) {
                        if (!event.likeOwnerIds.contains(authViewModel.data.value?.id)) {
                            viewModelEvent.likeById(event.id)
                        } else {
                            viewModelEvent.unlikeById(event.id)
                        }
                    }
                }
            }

            override fun onPlayMusic(event: Event, seekBar: SeekBar) {
                if (event.id != MainFragment.playId) {
                    MainFragment.playId = event.id
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
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = PostFragment.SIGN_IN
                        })
                } else {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, event.content)
                        type = "text/plain"
                    }
                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.share_content))
                    startActivity(shareIntent)
                }
            }

            override fun onImage(event: Event) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = PostFragment.SIGN_IN
                        })
                } else {
                    val url = event.attachment?.url
                    findNavController().navigate(
                        R.id.action_mainFragment_to_imageFragment3,
                        Bundle().apply {
                            textArg = url
                        })
                }
            }

        }, appAuth)

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

            if (state.loadError) {
                Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        viewModelEvent.loadEvents()
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
            if (state.saveError) {
                Snackbar.make(binding.root, R.string.failed_to_connect, Snackbar.LENGTH_LONG)
                    .setAction(R.string.ok) {}
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
            adapter.notifyDataSetChanged()
        }

        binding.addEvent.setOnClickListener {
            findNavController().navigate(R.id.newEventFragment)
        }

        return binding.root
    }


}