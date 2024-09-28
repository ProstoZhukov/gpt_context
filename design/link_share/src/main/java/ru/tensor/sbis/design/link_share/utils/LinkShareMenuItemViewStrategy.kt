package ru.tensor.sbis.design.link_share.utils

import ru.tensor.sbis.design.link_share.R
import ru.tensor.sbis.design.R as RDesign

/** Стратегия отображения элемента меню */
internal interface LinkShareMenuItemViewStrategy {
    fun getTextResId(): Int
    fun getIconResId(): Int
}

/** Реализация стратегии для элемента меню "Скопировать" */
internal class CopyLinkMenuItemStrategy : LinkShareMenuItemViewStrategy {
    override fun getTextResId(): Int = R.string.link_share_copy_link
    override fun getIconResId(): Int = RDesign.string.design_mobile_icon_link
}

/** Реализация стратегии для элемента меню "Открыть в браузере" */
internal class OpenInBrowserMenuItemStrategy : LinkShareMenuItemViewStrategy {
    override fun getTextResId(): Int = R.string.link_share_new_tab_link
    override fun getIconResId(): Int = RDesign.string.design_mobile_icon_www
}

/** Реализация стратегии для элемента меню QR-code*/
internal class OpenQRMenuItemStrategy : LinkShareMenuItemViewStrategy {
    override fun getTextResId(): Int = R.string.link_share_qr_link
    override fun getIconResId(): Int = RDesign.string.design_mobile_icon_qr
}

/** Реализация стратегии для элемента меню "Отправить" */
internal class SendLinkMenuItemStrategy : LinkShareMenuItemViewStrategy {
    override fun getTextResId(): Int = R.string.link_share_send_link
    override fun getIconResId(): Int = RDesign.string.design_mobile_icon_unload_new
}