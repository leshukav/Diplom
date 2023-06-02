package ru.netology.diplom.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.PopupMenu
import android.widget.SeekBar
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.diplom.R
import ru.netology.diplom.databinding.CardPostBinding
import ru.netology.diplom.dto.Post
import ru.netology.diplom.dto.TypeAttachment
import ru.netology.diplom.utils.Count

interface Listener {
    fun onClik(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
    fun onLike(post: Post)
    fun onPlayMusic(post: Post, seekBar: SeekBar)
    fun onPlayVideo(post: Post)
    fun onPause()

}

class PostAdapter(
    private val onListener: Listener
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.bind(post)
    }

}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onListener: Listener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            if (post.authorAvatar != null) {
                val url = post.authorAvatar
                authorAvatar.load(url)
            } else {
                authorAvatar.setImageResource(R.drawable.ic_error_100)
            }
            if (post.attachment == null) {
                image.visibility = View.GONE
                videoGroup.visibility = View.GONE
                audioGroup.visibility = View.GONE
            } else {
                when (post.attachment?.type) {
                    TypeAttachment.IMAGE -> {
                        videoGroup.visibility = View.GONE
                        audioGroup.visibility = View.GONE
                        image.visibility = View.VISIBLE
                        val url = post.attachment?.url
                        Glide.with(image)
                            .load(url)
                            .timeout(10_000)
                            .into(image)
                    }
                    TypeAttachment.VIDEO -> {
                        videoGroup.visibility = View.VISIBLE
                    }
                    TypeAttachment.AUDIO -> {
                        image.visibility = View.VISIBLE
                        audioGroup.visibility = View.VISIBLE
                        image.setImageResource(R.drawable.music_logo)
                        seekBar.max = 0
                    }
                    else -> {
                        image.visibility = View.GONE
                        videoGroup.visibility = View.GONE
                        audioGroup.visibility = View.GONE
                    }
                }


            }
            author.text = post.author
            publish.text = post.published
            content.text = post.content
            like.isChecked = post.likeByMe

            like.text =
                if (like.isChecked) Count.logic(post.likeOwnerIds.size + 1) else (Count.logic(post.likeOwnerIds.size))
            //    share.text = DisplayCount.logic(990)
            //   visibility.text = DisplayCount.logic(1)


            fabPlay.setOnClickListener {
                fabPlay.visibility = View.GONE
                videoView.apply {
                    setMediaController(MediaController(context))
                    setVideoURI(
                        Uri.parse(post.attachment?.url)
                    )
                    setOnPreparedListener {
                        start()
                    }
                    setOnCompletionListener {
                        stopPlayback()
                    }
                }
            }

            fabPlayAudio.setOnClickListener {
                if (fabPlayAudio.isChecked) {
                    onListener.onPlayMusic(post, seekBar)
                } else {
                    onListener.onPause()
                }
            }

            authorAvatar.setOnClickListener {
                onListener.onClik(post)
            }

            like.setOnClickListener {
                onListener.onLike(post)
            }

            menu.isVisible = post.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.option_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onListener.onEdit(post)
                                true
                            }
                            else -> false
                        }

                    }
                }.show()
            }
        }

    }

    fun ImageView.load(
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

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
