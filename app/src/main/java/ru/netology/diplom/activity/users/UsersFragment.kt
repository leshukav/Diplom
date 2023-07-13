package ru.netology.diplom.activity.users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.diplom.R
import ru.netology.diplom.adapter.UserAdapter
import ru.netology.diplom.adapter.onUserClick
import ru.netology.diplom.databinding.FragmentUsersBinding
import ru.netology.diplom.dto.User
import ru.netology.diplom.viewmodel.JobViewModel
import ru.netology.diplom.viewmodel.PostViewModel
import ru.netology.diplom.viewmodel.UserViewModel

@AndroidEntryPoint
class UsersFragment : Fragment() {
    private lateinit var binding: FragmentUsersBinding
    private lateinit var adapter: UserAdapter
    private val userViewModel: UserViewModel by activityViewModels()
    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModelPost: PostViewModel by activityViewModels()
    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModelJob: JobViewModel by activityViewModels()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(inflater)
        adapter = UserAdapter(object : onUserClick {
            override fun onClick(user: User) {
                viewModelPost.loadWallById(user.idUser)
                viewModelPost.loadUserData(user.idUser)
                viewModelJob.loadJobById(user.idUser)
                findNavController().navigate(R.id.authorFragment2)
            }

        })

        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter

        userViewModel.loadUsers()

        userViewModel.userData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.rcView.runWhenReady {
            binding.progress.isVisible = false
        }

        return binding.root
    }

}

fun RecyclerView.runWhenReady(action: () -> Unit) {
    val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            action()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    }
    viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
}