package ru.tensor.sbis.entrypoint_guard.activity.contract

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

/**
 * Контракт поведения, которое нужно добавить прикладной активности.
 *
 * @author kv.martyshenko
 */
interface ActivityBehaviour<in A : AppCompatActivity> {
    /**
     * Инициализация еще не завершена, использовать плагинную систему небезопасно.
     * Вызывается перед [super.onCreate].
     */
    fun onPreCreate(activity: A) = Unit

    /**
     * Вызывается после [super.onCreate] сразу после установки контента.
     * Инициализация все еще не завершена, использовать плагинную систему небезопасно.
     */
    fun onCreate(activity: A) = Unit

    /**
     * Активность [activity] полностью готова к использованию. Инициализация приложения прошла успешно.
     */
    fun onReady(activity: A, contentView: FrameLayout, savedState: Bundle?) = Unit
}