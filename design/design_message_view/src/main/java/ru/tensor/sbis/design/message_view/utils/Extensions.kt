package ru.tensor.sbis.design.message_view.utils

import ru.tensor.sbis.design.message_view.ui.MessageView

/**
 * Расширения для [MessageView].
 *
 * @author dv.baranov
 */

/** @SelfDocumented */
inline fun <reified T> Any.castTo(): T? = this as? T