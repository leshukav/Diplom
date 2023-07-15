package ru.netology.diplom.activity.job

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.diplom.databinding.FragmentNewJobBinding
import ru.netology.diplom.utils.AndroidUtils
import ru.netology.diplom.viewmodel.JobViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class NewJobFragment : Fragment() {
    lateinit var binding: FragmentNewJobBinding
    private val viewModelJob: JobViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewJobBinding.inflate(inflater)

        binding.saveJob.setOnClickListener {
            val nameJob = binding.name.text.toString()
            val linkJob = binding.link.text.toString()
            if (nameJob.isNotBlank() || linkJob.isNotBlank()) {
                this@NewJobFragment.viewModelJob.saveJob(nameJob, linkJob)
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            } else {
                Snackbar.make(binding.root, "Name and Link is not empty", Snackbar.LENGTH_LONG)
                    .setAction("Ok") {}
                    .show()
            }
        }

        return binding.root
    }

}