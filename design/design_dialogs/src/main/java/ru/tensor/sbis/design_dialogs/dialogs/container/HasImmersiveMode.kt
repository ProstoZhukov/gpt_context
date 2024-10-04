package ru.tensor.sbis.design_dialogs.dialogs.container

/**
 * Интерфейс, декларирующий возможность изменять состояние режима "погружения"
 * Режим "погружения" - скрытие системного UI, тулбара, других управляющих элементов, не относящихся к контенту
 *
 * @author sa.nikitin
 */
interface HasImmersiveMode {

    /**
     * Изменить поддержку режима "погружения"
     *
     * @param isImmersiveModeSupported  Поддерживается ли режим погружения
     *                                  Если передать false, то контролы нужно отобразить
     *                                  и более не скрывать до следующего вызова с true аргументом
     */
    fun changeImmersiveModeSupport(isImmersiveModeSupported: Boolean)

    /**
     * Изменить состояние режима "погружения"
     *
     * @param isImmersiveModeEnabled Включен ли режим "погружения"
     */
    fun changeImmersiveModeState(isImmersiveModeEnabled: Boolean)

    /**
     * Переключить состояние режима "погружения" с текущего на противоположное
     */
    fun switchImmersiveModeState()
}