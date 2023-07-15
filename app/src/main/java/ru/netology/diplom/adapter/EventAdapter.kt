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
import ru.netology.diplom.databinding.CardEventBinding
import ru.netology.diplom.dto.Event
import ru.netology.diplom.dto.Type
import ru.netology.diplom.dto.TypeAttachment
import ru.netology.diplom.utils.Count

class EventAdapter(
    private val onListener: OnClick<Event>,
    private val auth: AppAuth
) : PagingDataAdapter<Event, EventViewHolder>(EventDiffCallback()) {
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position) ?: return
        holder.bind(event)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onListener, auth)
    }
}

class EventViewHolder(
    private val binding: CardEventBinding,
    private val onListener: OnClick<Event>,
    private val auth: AppAuth
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(event: Event) {
        binding.apply {

            val split = event.published.split(".")
            dateEvent.text = split[0]
            author.text = event.author
            content.text = event.content
            job.text = event.authorJob
            if (event.authorAvatar != null) {
                val url = event.authorAvatar
                avatar.load(url)
            } else {
                avatar.setImageResource(R.drawable.ic_error_100)
            }
            if (event.types == Type.OFFLINE) {
                type.setImageResource(R.drawable.circle_offline)
            } else if (event.types == Type.ONLINE) {
                type.setImageResource(R.drawable.circle_online)
            }

            if (event.attachment == null) {
                image.visibility = View.GONE
                videoGroup.visibility = View.GONE
                audioGroup.visibility = View.GONE
            } else {
                when (event.attachment?.type) {
                    TypeAttachment.IMAGE -> {
                        videoGroup.visibility = View.GONE
                        audioGroup.visibility = View.GONE
                        image.visibility = View.VISIBLE
                        val url = event.attachment?.url
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
                            Uri.parse(event.attachment?.url)
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
            like.isCheckable = auth.getAuthId() != 0L
            like.isChecked = event.likeOwnerIds.contains(auth.getAuthId())
            like.text = Count.logic(event.likeOwnerIds.size)
            members.text = Count.logic(event.participantsIds.size)

            fabPlay.setOnClickListener {
                fabPlay.visibility = View.GONE
                videoView.apply {
                    setMediaController(MediaController(context))
                    setVideoURI(
                        Uri.parse(event.attachment?.url)
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
                    onListener.onPlayMusic(event, seekBar)
                } else {
                    onListener.onPause()
                }
            }

            avatar.setOnClickListener {
                onListener.onClik(event)
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
                onListener.onLike(event)
            }

            share.setOnClickListener {
                onListener.onShare(event)
            }

            image.setOnClickListener {
                onListener.onImage(event)
            }

            menu.isVisible = event.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.option_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onListener.onRemove(event)
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


class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}