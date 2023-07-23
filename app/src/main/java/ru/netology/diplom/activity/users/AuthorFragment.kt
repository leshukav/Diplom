package ru.netology.diplom.activity.users

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
import ru.netology.diplom.activity.job.JobFragment
import ru.netology.diplom.activity.wall.WallFragment
import ru.netology.diplom.adapter.ViewPagerAdapter
import ru.netology.diplom.adapter.loadAvatar
import ru.netology.diplom.databinding.FragmentAuthorBinding
import ru.netology.diplom.viewmodel.UserViewModel
import ru.netology.diplom.viewmodel.WallViewModel

@AndroidEntryPoint
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AuthorFragment(): Fragment(), MenuProvider {

    lateinit var binding: FragmentAuthorBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val wallViewModel: WallViewModel by activityViewModels()


    private val fragmentList = listOf(
        WallFragment(),
        JobFragment()
    )
    private val tabList = listOf(
        "POSTS",
        "JOBS",
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthorBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        init()
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.authorUser.text = user.name
            if (user.avatar != null) {
                val url = user.avatar
                binding.userAvatar.loadAvatar(url)
            } else {
                binding.userAvatar.setImageResource(R.drawable.ic_error_100)
            }
        }

    }

    private fun init() {
        val adapter = ViewPagerAdapter(activity as FragmentActivity, fragmentList)
        binding.vp.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.vp) { tab, pos ->
            tab.text = tabList[pos]
        }.attach()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_author, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
        when (menuItem.itemId) {
            android.R.id.home -> {
                wallViewModel.removeWallDao()
                findNavController().navigateUp()
                true
            }
            else -> false

        }

}