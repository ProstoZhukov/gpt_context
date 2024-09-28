package ru.tensor.sbis.design.video_message_view.preview.binding

import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.video_message_view.preview.VideoPreview
import ru.tensor.sbis.design.video_message_view.preview.data.VideoPreviewData

@BindingAdapter("setData")
fun VideoPreview.setData(data: VideoPreviewData) {
    this.data = data
}
