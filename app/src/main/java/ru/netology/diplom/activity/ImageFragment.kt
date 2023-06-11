package ru.netology.diplom.activity

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import ru.netology.diplom.R
import ru.netology.diplom.activity.MainFragment.Companion.textArg
import ru.netology.diplom.databinding.FragmentImageBinding

class ImageFragment : Fragment() {
    private lateinit var binding: FragmentImageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageBinding.inflate(inflater, container, false)

        arguments?.textArg?.let {
            val uri: Uri = Uri.parse(it)
            Glide.with(binding.imageFragment)
                .load(uri)
                .timeout(10_000)
                .into(binding.image)
        }
        return binding.root
    }

}