package ru.netology.diplom.activity.job

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.diplom.R
import ru.netology.diplom.adapter.JobAdapter
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.databinding.FragmentJobBinding
import ru.netology.diplom.viewmodel.AuthViewModel
import ru.netology.diplom.viewmodel.JobViewModel
import ru.netology.diplom.viewmodel.PostViewModel

@AndroidEntryPoint
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class JobFragment : Fragment() {
    lateinit var binding: FragmentJobBinding
    lateinit var adapter: JobAdapter
    private val viewModelJob: JobViewModel by activityViewModels()
    private val viewModelAuth: AuthViewModel by activityViewModels()
    private val viewModelPost: PostViewModel by activityViewModels()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentJobBinding.inflate(inflater)

        adapter = JobAdapter(AppAuth(requireContext()))

        viewModelPost.userData.observe(viewLifecycleOwner) { user ->
            binding.addJob.isVisible = viewModelAuth.data.value?.id == user.idUser
        }

        binding.jobRv.layoutManager = LinearLayoutManager(activity)
        binding.jobRv.adapter = adapter
        viewModelJob.jobData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (it.isEmpty()) {
                binding.jobEmpty.isVisible = true
                binding.jobEmpty.text = getString(R.string.job_not_found)
            }
        }

        binding.addJob.setOnClickListener {
            findNavController().navigate(R.id.newJobFragment)
        }

        return binding.root
    }

}