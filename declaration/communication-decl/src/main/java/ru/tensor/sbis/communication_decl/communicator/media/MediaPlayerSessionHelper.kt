package ru.tensor.sbis.communication_decl.communicator.media

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

/**
 * Вспомогательная реализация для настройки сессии проигрывания.
 */
interface MediaPlayerSessionHelper {

    /**
     * Инициализировать вспомогательную реализацию по настройке плеера для сессии,
     * в которой может начаться проигрывание.
     * Сессия завершится автоматически на destroy view фрагмента.
     *
     * @param fragment фрагмент, за жизненным циклом которого будет закреплена сессия.
     * @param customPlayer кастомный плеер, по умолчанию используется дефолтный плеер.
     */
    fun init(fragment: Fragment, customPlayer: MediaPlayer? = null)

    /**
     * Инициализировать вспомогательную реализацию по настройке плеера для сессии,
     * в которой может начаться проигрывание.
     * Сессия завершится автоматически на destroy [lifecycleOwner].
     *
     * @param lifecycleOwner компонент, к жизненному циклу которого происходит привязка сессий.
     * @param activity активити экрана, необходима для настройки динамиков [Activity.setVolumeControlStream].
     * @param customPlayer кастомный плеер, по умолчанию используется дефолтный плеер.
     */
    fun init(
        lifecycleOwner: LifecycleOwner,
        activity: Activity? = null,
        customPlayer: MediaPlayer? = null
    )
}