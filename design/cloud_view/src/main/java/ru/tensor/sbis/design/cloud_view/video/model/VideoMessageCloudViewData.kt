package ru.tensor.sbis.design.cloud_view.video.model

import android.text.Spannable

/**
 * Бизнес модель сообщения для [VideoMessageMediaContent]
 *
 * @author da.zhukov
 */
interface VideoMessageCloudViewData {

    /**
     * Контент видеосообщения.
     */
    val content: List<VideoMessageContent>

    /**
     * Текст в облачке для отображения в компоненте [RichTextView]. Может быть только один блок текста
     */
    val text: Spannable?
}