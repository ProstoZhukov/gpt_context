package ru.tensor.sbis.info_decl.news.ui.config.wall

import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * Конфигурация компонента для показа ленты новостей на стене.
 *
 * @property channelUuid идентификатор группы соц. сети, по которой будет происходить загрузка новостей
 * @property limitMode режим ограничения вывода ленты новостей
 *
 * @author am.boldinov
 */
class WallNewsConfiguration(
    val channelUuid: UUID,
    val limitMode: LimitMode = LimitMode.Infinite
)

/**
 * Режим ограничения вывода ленты новостей на стене.
 */
sealed class LimitMode {

    /**
     * Без ограничений, бесконечная лента с постраничной загрузкой.
     */
    object Infinite : LimitMode()

    /**
     * Ограниченный вывод ленты новостей.
     *
     * @property limit максимальное количество новостей для отображения в ленте
     * @property limitMoreViewFactory фабрика по созданию View для индикации наличия новостей сверх лимита ("Есть еще")
     * или добавлению кнопки для показа всех новостей на стене, например открытие нового экрана через
     * [ru.tensor.sbis.info_decl.news.ui.NewsListFragmentProvider.getWallNewsListFragment]
     */
    class Limited(val limit: Int, val limitMoreViewFactory: (ViewGroup) -> View) : LimitMode()

}

