package ru.netology.diplom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.databinding.CardJobBinding
import ru.netology.diplom.dto.Job

class JobAdapter(
    private val auth: AppAuth,
): ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding, auth)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position) ?: return
        holder.bind(job)
    }

}

class JobViewHolder(
    private val binding: CardJobBinding,
    private val auth: AppAuth,
): RecyclerView.ViewHolder(binding.root){
    fun bind(job: Job) {
        binding.apply {
            name.text = job.name
            position.text = job.position
            startTime.text = job.start
            finishTime.text = job.finish
            menu.isVisible = auth.getAuthId() ==  job.id
            link.text = job.link

        }
    }

}

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }

}