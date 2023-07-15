package ru.netology.diplom.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.MediaController
import android.widget.PopupMenu
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.diplom.R
import ru.netology.diplom.auth.AppAuth
import ru.netology.diplom.databinding.CardPostBinding
import ru.netology.diplom.dto.Post
import ru.netology.diplom.dto.TypeAttachment
import ru.netology.diplom.utils.Count

class PostAdapter(
    private val onListener: OnClick<Post>,
    private val auth: AppAuth
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onListener, auth)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onListener: OnClick<Post>,
    private val auth: AppAuth
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {

            val split = post.published.split(".")
            publish.text = split[0]
            author.text = post.author
            content.text = post.content
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
                        image.visibility = View.GONE
                        audioGroup.visibility = View.GONE
                        videoGroup.visibility = View.VISIBLE
                        videoView.setVideoURI(
                            Uri.parse(post.attachment?.url)
                        )
                        videoView.seekTo(10)
                    }
                    TypeAttachment.AUDIO -> {
                        image.visibility = View.VISIBLE
                        audioGroup.visibility = View.VISIBLE
                        videoGroup.visibility = View.GONE
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
                if (auth.getAuthId() != 0L) {
                    val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1F, 1.5F, 1F)
                    val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1F, 1.5F, 1F)
                    ObjectAnimator.ofPropertyValuesHolder(it, scaleX, scaleY).apply {
                        duration = 1000
                        repeatCount = 1
                        interpolator = BounceInterpolator()
                    }.start()
                }
                onListener.onLike(post)
            }
            like.isCheckable = auth.getAuthId() != 0L
            like.isChecked = post.likeOwnerIds.contains(auth.getAuthId())
            like.text = Count.logic(post.likeOwnerIds.size)

            share.setOnClickListener {
                onListener.onShare(post)
            }

            image.setOnClickListener {
                onListener.onImage(post)
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
                            else -> false
                        }

                    }
                }.show()
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

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}