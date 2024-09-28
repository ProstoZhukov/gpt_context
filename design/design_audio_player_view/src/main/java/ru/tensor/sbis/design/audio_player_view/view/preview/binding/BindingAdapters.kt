package ru.tensor.sbis.design.audio_player_view.view.preview.binding

import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.audio_player_view.view.preview.AudioPreview
import ru.tensor.sbis.design.audio_player_view.view.preview.data.AudioPreviewData

@BindingAdapter("setData")
fun AudioPreview.setData(data: AudioPreviewData) {
    this.data = data
}