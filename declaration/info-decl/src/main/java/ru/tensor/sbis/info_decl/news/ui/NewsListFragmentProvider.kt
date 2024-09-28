package ru.tensor.sbis.info_decl.news.ui

import android.view.View
import androidx.fragment.app.Fragment
import ru.tensor.sbis.info_decl.news.ui.config.wall.WallNewsConfiguration
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/**
 * Поставщик главного фрагмента модуля новостей
 *
 * @author sr.golovkin on 27.04.2020
 */
interface NewsListFragmentProvider : Feature {

    /**
     * Создать новый экземпляр главного фрагмента модуля новостей.
     */
    fun getNewsListFragment(): Fragment

    /**
     * Создать новый экземпляр фрагмента реестра новостей на основе внешней конфигурации интерфейса.
     * Фрагмент содержит только вертикальный список новостей, остальные вью интегрируются через конфигурацию.
     */
    fun getConfigNewsListFragment(): Fragment

    /**
     * Создать новый экземпляр компонента для рендера новостей на стене.
     * Представляет из себя горизонтальный список новостей, автоматически загружает контент и обрабатывает клики.
     * В процессе загрузки показывает прогрессбар, в случае неудачи загрузки покажет заглушку.
     *
     * @param host фрагмент, в котором будет находится компонент. Необходим для привязки к жизненному циклу [androidx.lifecycle.Lifecycle]
     * @param config конфигурация View для показа ленты новостей на стене
     */
    fun getWallNewsLayout(host: Fragment, config: WallNewsConfiguration): View

    /**
     * Создать новый экземпляр компонента для рендера новостей со стены на отдельном экране.
     * Представляет из себя ленту новостей определенной группы.
     *
     * @param channelUuid - идентификатор группы, новости которой будут отображаться на экране
     */
    fun getWallNewsListFragment(channelUuid: UUID): Fragment
}