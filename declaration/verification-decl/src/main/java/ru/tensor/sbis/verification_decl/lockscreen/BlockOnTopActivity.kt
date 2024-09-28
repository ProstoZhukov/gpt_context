package ru.tensor.sbis.verification_decl.lockscreen

/**
 * Вид активностей EntryPointActivity, для которых мы не будем звать finish(),
 * а дадим им отрисоваться и откроем блокировку поверх при запуске и необходимости.
 *
 * Например, в случае ShareMenuActivity обычная схема не завелась
 * из-за ошибки SecurityException при попытке запуска ее исходного интента при разблокировке.
 *
 * @author ar.leschev
 */
interface BlockOnTopActivity