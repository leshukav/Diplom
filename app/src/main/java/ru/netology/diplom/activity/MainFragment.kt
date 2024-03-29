package ru.netology.diplom.activity

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.activity.event.EventFragment
import ru.netology.diplom.activity.post.PostFragment
import ru.netology.diplom.activity.users.UsersFragment
import ru.netology.diplom.adapter.ViewPagerAdapter
import ru.netology.diplom.databinding.FragmentMainBinding
import ru.netology.diplom.utils.MediaLifecycleObserver
import ru.netology.diplom.utils.StringArgs
import ru.netology.diplom.viewmodel.*

@AndroidEntryPoint
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class MainFragment() : Fragment(), MenuProvider {
    companion object {
        var Bundle.textArg by StringArgs
        val observer = MediaLifecycleObserver()
        var playId = -1L
    }

    lateinit var binding: FragmentMainBinding
    private val authViewModel: AuthViewModel by activityViewModels()
    private val wallViewModel: WallViewModel by activityViewModels()
    private val viewModelJob: JobViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private val fragmentList = listOf(
        PostFragment(),
        EventFragment(),
        UsersFragment()
    )
    private val tabList = listOf(
        "POSTS",
        "EVENT",
        "USERS"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        lifecycle.addObserver(observer)
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater)

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
                findNavController().navigate(R.id.logoutFragment,
                    Bundle().apply {
                        textArg = PostFragment.SIGN_OUT
                    })
                true
            }
            R.id.myWall -> {
                wallViewModel.loadMyWall()
                viewModelJob.loadMyJob()
                authViewModel.data.value?.let {
                    userViewModel.loadUserData(it.id)
                }
                findNavController().navigate(R.id.authorFragment2)
                true
            }
            else -> false
        }

    private fun init() {
        val adapter = ViewPagerAdapter(activity as FragmentActivity, fragmentList)
        binding.vp.adapter = adapter
        TabLayoutMediator(binding.mainLayout, binding.vp) { tab, pos ->
            tab.text = tabList[pos]
        }.attach()
    }

}