package ru.tensor.sbis.verification_decl.lockscreen

/**
 * Фиксатор активности пользователя.
 *
 * Записывает значение временной метки при вызове [activityFixed], который
 * стоит вызывать на интересующей активности в методе onUserInteraction.
 *
 * @author ar.leschev
 */
interface ActivityCollector {
    /**
     * Зафиксировать пользовательскую активность.
     * Метод должен писать в память значения.
     */
    fun activityFixed()

    /**
     * Учитывается ли активность [isEnabled].
     */
    fun setEnabled(isEnabled: Boolean)
}