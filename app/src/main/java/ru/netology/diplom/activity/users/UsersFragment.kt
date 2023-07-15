package ru.netology.diplom.activity.users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.diplom.R
import ru.netology.diplom.activity.MainFragment.Companion.textArg
import ru.netology.diplom.activity.post.PostFragment
import ru.netology.diplom.adapter.OnUserClick
import ru.netology.diplom.adapter.UserAdapter
import ru.netology.diplom.databinding.FragmentUsersBinding
import ru.netology.diplom.dto.User
import ru.netology.diplom.viewmodel.*

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class UsersFragment : Fragment() {
    private lateinit var binding: FragmentUsersBinding
    private lateinit var adapter: UserAdapter
    private val userViewModel: UserViewModel by activityViewModels()
    private val wallViewModel: WallViewModel by activityViewModels()
    private val viewModelJob: JobViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(inflater)
        adapter = UserAdapter(object : OnUserClick {
            override fun onClick(user: User) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = PostFragment.SIGN_IN
                        })
                } else {
                    wallViewModel.loadWallById(user.idUser)
                    userViewModel.loadUserData(user.idUser)
                    viewModelJob.loadJobById(user.idUser)
                    findNavController().navigate(R.id.authorFragment2)
                }
            }

        })

        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter

        userViewModel.loadUsers()

        userViewModel.userData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        userViewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading

            if (state.loadError) {
                Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        userViewModel.loadUsers()
                    }
                    .show()
            }
        }

        binding.swiperefresh.setOnRefreshListener {
            userViewModel.loadUsers()
        }

        return binding.root
    }

}
