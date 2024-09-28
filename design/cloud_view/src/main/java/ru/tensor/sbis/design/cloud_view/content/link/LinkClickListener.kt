package ru.tensor.sbis.design.cloud_view.content.link

import ru.tensor.sbis.design.cloud_view.CloudView

/**
 * Подписка на нажатие по ссылке в [CloudView]
 *
 * @author ma.kolpakov
 */
interface LinkClickListener {

    fun onLinkClicked()
}