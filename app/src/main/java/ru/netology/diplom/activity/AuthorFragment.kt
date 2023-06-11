package ru.netology.diplom.activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import ru.netology.diplom.adapter.ViewPagerAdapter
import ru.netology.diplom.databinding.FragmentAuthorBinding

class AuthorFragment : Fragment() {

    lateinit var binding: FragmentAuthorBinding

    private val fragmentList = listOf(
        Fragment(),
        JobFragment(),
        MediaFragment()
    )
    private val tabList = listOf(
        "POSTS",
        "JOBS",
        "MEDIA",
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    //    super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        val adapter = ViewPagerAdapter(activity as FragmentActivity, fragmentList)
        binding.vp.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.vp) { tab, pos ->
            tab.text = tabList[pos]
        }.attach()
    }

}