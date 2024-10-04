package ru.tensor.sbis.design.cloud_view.content.grant_access

import android.view.View
import ru.tensor.sbis.design.cloud_view.CloudView

/**
 * Обработчики запроса доступа к файлу в [CloudView]
 *
 * @author rv.krohalev
 */
interface GrantAccessActionListener {
    /**
     * Обработка разрешения доступа - будет показано меню с уровнями доступа
     */
    fun onGrantAccessClicked(sender: View)

    /**
     * Обработка отказа давать доступ к файлу
     */
    fun onDenyAccessClicked()
}