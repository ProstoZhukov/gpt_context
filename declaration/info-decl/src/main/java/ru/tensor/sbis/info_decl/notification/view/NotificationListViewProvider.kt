package ru.tensor.sbis.info_decl.notification.view

import android.view.View
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс поставщика встраиваемой View со списком уведомлений.
 *
 * @author am.boldinov
 */
interface NotificationListViewProvider : Feature {

    /**
     * Получить View списка уведомлений для встраивания в иеархию экрана,
     * например в качестве ячейки [androidx.recyclerview.widget.RecyclerView].
     * @param host фрагмент, который выступает в качестве LifecycleOwner и ViewModelStore
     * @param configuration конфигурация отображения и загрузки списка уведомлений
     *
     * @return View со списком уведомлений
     */
    fun getNotificationListView(host: Fragment, configuration: NotificationListViewConfiguration): View
}