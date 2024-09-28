package ru.tensor.sbis.edo_decl.doc_opener

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature


/**
 * Opener документов
 * Для получения экземпляра в прикладном модуле объявите этот интерфейс зависимостью плагина модуля
 *
 * @author sa.nikitin
 */
interface DocOpener : Feature {

    /**
     * Открыть документ по запросу [request]
     * С помощью [context] будет запущен новый экран
     */
    fun open(context: Context, request: OpenDocRequest)

    /**
     * Создать намерение на открытие opener-а документов по запросу [request]
     */
    fun createOpenIntent(context: Context, request: OpenDocRequest): Intent

    /**
     * Создать фрагмент opener-а документов
     */
    fun createOpenerFragment(request: OpenDocRequest): Fragment
}