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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.diplom.R
import ru.netology.diplom.adapter.JobAdapter
import ru.netology.diplom.adapter.OnClickMenu
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.databinding.FragmentJobBinding
import ru.netology.diplom.dto.Job
import ru.netology.diplom.viewmodel.AuthViewModel
import ru.netology.diplom.viewmodel.JobViewModel
import ru.netology.diplom.viewmodel.UserViewModel

@AndroidEntryPoint
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class JobFragment : Fragment() {
    lateinit var binding: FragmentJobBinding
    lateinit var adapter: JobAdapter
    private val viewModelJob: JobViewModel by activityViewModels()
    private val viewModelAuth: AuthViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentJobBinding.inflate(inflater)

        adapter = JobAdapter(AppAuth(requireContext()), userViewModel, object : OnClickMenu {
            override fun onClick(job: Job) {
                viewModelJob.removeMyJobById(job.id)
            }
        })

        userViewModel.user.observe(viewLifecycleOwner) { user ->
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

        viewModelJob.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading

            if (state.loadError) {
                Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                    }
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
        }

        binding.addJob.setOnClickListener {
            findNavController().navigate(R.id.newJobFragment)
        }
        return binding.root
    }
}