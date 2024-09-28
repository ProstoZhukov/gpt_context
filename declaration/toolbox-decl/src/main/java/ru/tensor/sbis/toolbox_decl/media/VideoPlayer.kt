package ru.tensor.sbis.toolbox_decl.media

import android.content.Context
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс видеопроигрывателя
 *
 * @author sa.nikitin
 */
interface VideoPlayer : Feature {

    /**
     * Поддерживается ли формат видео, т.е. может ли видеоплеер его воспроизвести
     *
     * @param extension Расширение видео (без точки)
     */
    fun isSupportedFormat(extension: String): Boolean

    /**
     * Проиграть видео по Uri
     * Если формат видео неподдерживаемый, т.е. [isSupportedFormat] == false, то будет ошибка воспроизведения
     *
     * @param context                   Вызывающий контекст
     * @param uri                       Uri, ссылающийся на видео. Поддерживаются локальные и сетевые Uri
     * @param name                      Название видео. Отображается пользователю
     * @param replaceCloseByBackArrow   Заменить ли кнопку закрытия плеера на стрелку "Назад"
     */
    fun playVideoByUri(context: Context, uri: String, name: String, replaceCloseByBackArrow: Boolean)

    /**
     * Проиграть видео по ссылке
     *
     * @param context                   Вызывающий контекст
     * @param fragmentManager           FragmentManager для отображения диалога скачивания видео
     * @param nameWithExtension         Название видео с расширением
     * @param url                       Ссылка на видео
     * @param replaceCloseByBackArrow   Заменить ли кнопку закрытия плеера на стрелку "Назад"
     */
    fun playVideoByUrl(
        context: Context,
        fragmentManager: FragmentManager,
        nameWithExtension: String,
        url: String,
        replaceCloseByBackArrow: Boolean
    )

    /**
     * Проиграть видео, принадлежащего объекту бизнес-логики "FileSD", упрощённо говоря СБИС.Диск-у
     *
     * @param context               Вызывающий контекст
     * @param fragmentManager       FragmentManager для отображения диалога скачивания видео
     * @param fileSDUuid            Идентификатор FileSD-видео
     * @param nameWithExtension     Название видео с расширением
     */
    fun playFileSDVideo(
        context: Context,
        fragmentManager: FragmentManager,
        fileSDUuid: String,
        nameWithExtension: String
    )

    /**
     * Проиграть видео по абсолютному пути
     *
     * @param context           Вызывающий контекст
     * @param absolutePath      Абсолютный путь до видео во внутренней памяти устройства
     */
    fun playVideoByPath(context: Context, absolutePath: String)
}