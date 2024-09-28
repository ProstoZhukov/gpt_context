package ru.tensor.sbis.edo_decl.e_signatures

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.toolbox_decl.counters.CounterProvider

/**
 * Поставщик счётчика непрочитанных уведомлений об электронных подписях
 *
 * @author us.bessonov
 */
interface ESignsUnreadCounterProvider : CounterProvider<Int>, Feature