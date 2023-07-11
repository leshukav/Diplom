package ru.netology.diplom.activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.activity.wall.WallFragment
import ru.netology.diplom.adapter.ViewPagerAdapter
import ru.netology.diplom.adapter.loadAvatar
import ru.netology.diplom.databinding.FragmentAuthorBinding
import ru.netology.diplom.viewmodel.PostViewModel

@AndroidEntryPoint
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AuthorFragment : Fragment() {

    lateinit var binding: FragmentAuthorBinding
    private val viewModelPost: PostViewModel by activityViewModels()

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
    ): View? {
        binding = FragmentAuthorBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        viewModelPost.userData.observe(viewLifecycleOwner) { user ->
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

}