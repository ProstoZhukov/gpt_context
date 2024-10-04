package ru.tensor.sbis.design.audio_player_view.view.message.data

import androidx.annotation.DrawableRes
import org.json.JSONObject
import ru.tensor.sbis.design.audio_player_view.R
import timber.log.Timber

/**
 * Типы эмоций для аудио сообщения.
 *
 * @property code код эмоции, может не совпадать с порядком в перечислении.
 * @property drawableResId ресурс картинки смайлика.
 *
 * @author rv.krohalev
 */
enum class AudioMessageEmotion(
    val code: Int,
    @DrawableRes val drawableResId: Int?
) {

    /**
     * Без эмоции.
     */
    DEFAULT(0, null),

    /**
     * С плачущим смайликом.
     */
    CRYING(1, R.drawable.crying_face),

    /**
     * Со злым смайликом.
     */
    POUTING(2, R.drawable.pouting_face),

    /**
     * С улыбчивым смайликом.
     */
    SMILING(3, R.drawable.smiling_face),

    /**
     * С задумчивым смайликом.
     */
    THINKING(4, R.drawable.thinking_face);

    companion object {
        @JvmStatic
        fun getAudioMessageEmotion(serviceObject: JSONObject): AudioMessageEmotion {
            val emotionCode = serviceObject.optInt("emotion")
            val emotion: AudioMessageEmotion
            val emotions = values()
            emotion = if (emotionCode >= 0 && emotionCode < emotions.size) {
                emotions[emotionCode]
            } else {
                Timber.e("Unexpected emotionCode %s", emotionCode)
                DEFAULT
            }
            return emotion
        }
    }
}
