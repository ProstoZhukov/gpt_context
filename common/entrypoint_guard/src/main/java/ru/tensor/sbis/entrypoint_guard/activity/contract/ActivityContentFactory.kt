package ru.tensor.sbis.entrypoint_guard.activity.contract

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

/**
 * Контракт обработки создания активности [T].
 *
 * @author kv.martyshenko
 */
fun interface ActivityContentFactory<in T : AppCompatActivity> {

    /**
     * Активность готова к использованию.
     *
     * [AppCompatActivity.setContentView] уже вызван.
     * Стоит добавить контент в [parent] при необходимости.
     * Безопасно добавлять обработчики новых Intent`ов, BackPressedDispatcher и прочие.
     */
    fun create(
        activity: T,
        parent: FrameLayout,
        savedInstanceState: Bundle?
    )

}