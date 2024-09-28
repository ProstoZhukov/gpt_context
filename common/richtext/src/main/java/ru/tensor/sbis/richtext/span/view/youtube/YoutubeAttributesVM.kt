package ru.tensor.sbis.richtext.span.view.youtube

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.richtext.span.view.BaseAttributesVM
import ru.tensor.sbis.richtext.view.RichViewLayout
import ru.tensor.sbis.richtext.R

/**
 * Вью-модель атрибутов превью видео с ютуб.
 *
 * @author am.boldinov
 */
internal class YoutubeAttributesVM(tag: String, private val videoId: String?) : BaseAttributesVM(tag) {

    override fun createViewHolderFactory(): RichViewLayout.ViewHolderFactory {
        return RichViewLayout.ViewHolderFactory { parent ->
            val inflater = LayoutInflater.from(parent.context)
            YoutubeViewHolder(
                inflater.inflate(
                    R.layout.richtext_youtube_video,
                    parent as ViewGroup,
                    false
                )
            )
        }
    }

    private class YoutubeViewHolder(view: View) : RichViewLayout.ViewHolder<YoutubeAttributesVM>(view) {

        private val previewer = view.findViewById<YouTubePreviewer>(R.id.richtext_youtube_thumbnail).apply {
            setOnPreviewClickListener { videoId ->
                YouTubeUtil.playYouTubeVideo(view.context, videoId)
            }
        }

        override fun onPreMeasure(attributesVM: YoutubeAttributesVM, maxWidth: Int, maxHeight: Int) {
            previewer.setMaxPreviewHeightInPx(maxHeight)
        }

        override fun bind(attributesVM: YoutubeAttributesVM) {
            previewer.setVideoId(attributesVM.videoId)
        }

    }

}