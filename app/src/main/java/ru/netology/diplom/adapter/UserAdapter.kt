package ru.netology.diplom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.diplom.R
import ru.netology.diplom.databinding.CardUserBinding
import ru.netology.diplom.dto.User

interface OnUserClick {
    fun onClick(user: User)
}

class UserAdapter(
    private val onListener: OnUserClick
) : ListAdapter<User, UserViewHolder>(UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position) ?: return
        holder.bind(user)
    }

}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val onListener: OnUserClick
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: User) {
        binding.apply {
            name.text = user.name
            if (user.avatar != null) {
                val url = user.avatar
                avatar.load(url)
            } else {
                avatar.setImageResource(R.drawable.ic_error_100)
            }
            cardUser.setOnClickListener {
                onListener.onClick(user)
            }

        }
    }

    private fun ImageView.load(
        url: String,
        @DrawableRes placeholder: Int = R.drawable.ic_loading_100dp,
        @DrawableRes fallBack: Int = R.drawable.ic_error_100,
        timeOutMs: Int = 10_000
    ) {
        Glide.with(this)
            .load(url)
            .placeholder(placeholder)
            .error(fallBack)
            .timeout(timeOutMs)
            .circleCrop()
            .into(this)
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.idUser == newItem.idUser
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

}